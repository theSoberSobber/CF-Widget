import asyncio
import json
import os
from datetime import datetime
from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException
from fastapi.responses import JSONResponse, RedirectResponse
from scraper import scrape_recent_actions
from typing import Dict, Any, Optional, List
import httpx
import subprocess
import time

# Global variables
last_update = None
cached_data = None
cached_filtered_data = None  # Cache for filtered data
update_lock = asyncio.Lock()

model = {
    "name": "llama3.2",
    "parameters": "3b"
}

ollama_identifier = f"{model["name"]}:{model["parameters"]}"

async def pull_ollama_model():
    print(f"Pulling {ollama_identifier} model...")
    try:
        async with httpx.AsyncClient() as client:
            async with client.stream(
                "POST",
                "http://ollama:11434/api/pull",
                json={
                    "name": ollama_identifier
                }
            ) as response:
                response.raise_for_status()
                async for line in response.aiter_lines():
                    if line.strip():
                        try:
                            data = json.loads(line)
                            if "status" in data:
                                print(f"Pull status: {data['status']}")
                            if "digest" in data:
                                print(f"Digest: {data['digest']}")
                            if "total" in data and "completed" in data:
                                print(f"Progress: {data['completed']}/{data['total']} layers")
                        except json.JSONDecodeError:
                            print(f"Raw response: {line}")
                print("Model pulled successfully!")
                return True
    except Exception as e:
        print(f"Error pulling model: {e}")
        return False

async def wait_for_ollama():
    """Wait for Ollama service to be ready"""
    max_retries = 30  # 5 minutes with 10-second intervals
    retry_count = 0
    
    while retry_count < max_retries:
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get("http://ollama:11434/")
                if response.status_code == 200:
                    print("Ollama service is ready!")
                    return True
        except Exception as e:
            print(f"Waiting for Ollama service... ({retry_count + 1}/{max_retries}) - {e}")
        
        await asyncio.sleep(10)
        retry_count += 1
    
    print("Ollama service failed to start within timeout")
    return False

async def is_technical_content(title: str, max_retries: int = 3) -> bool:
    prompt = f"""Determine if this Codeforces blog title is technical in nature. By technical we mean if it will be beneficial to the reader in expanding their competitive programming skill. Let is also be called technical if it's a contest announcement or an editorial or similar competitive programming news. Unnecessary things that are personal like how do I progress, or an event in someone's life are not be allowed.
    Title: {title}
    Answer with just 'yes' or 'no'."""
    
    for attempt in range(max_retries):
        try:
            async with httpx.AsyncClient() as client:
                response = await client.post(
                    "http://ollama:11434/api/generate",
                    json={
                        "model": ollama_identifier,
                        "prompt": prompt,
                        "stream": False
                    }
                )
                response.raise_for_status()
                result = response.json()
                print(title, result["response"].lower())
                return "yes" in result["response"].lower()
        except Exception as e:
            print(f"Error calling Ollama API (attempt {attempt + 1}/{max_retries}): {e}")
            if attempt == max_retries - 1:
                print(f"All retry attempts failed for title: {title}")
                return False
            await asyncio.sleep(1)  # Wait before retrying
    return False

async def filter_technical_content(actions: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
    filtered_actions = []
    for action in actions:
        if await is_technical_content(action["blog_title"]):
            filtered_actions.append(action)
    return filtered_actions

# Global variable to track filtering status
is_filtering_in_progress = False

async def background_filter_content():
    global cached_filtered_data, is_filtering_in_progress
    try:
        is_filtering_in_progress = True
        filtered_actions = await filter_technical_content(cached_data["recent_actions"])
        cached_filtered_data = {"recent_actions": filtered_actions}
    finally:
        is_filtering_in_progress = False

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup logic
    print("Starting up API...")
    
    # Wait for Ollama service to be ready
    if not await wait_for_ollama():
        raise RuntimeError("Failed to start: Ollama service not available")
    
    # Pull the model
    if not await pull_ollama_model():
        raise RuntimeError("Failed to start: Could not pull Ollama model")
    
    # Initialize data
    await ensure_data_exists()
    task = asyncio.create_task(periodic_update())
    
    yield  # This is where the app runs
    
    # Shutdown logic
    task.cancel()
    try:
        await task
    except asyncio.CancelledError:
        pass

app = FastAPI(
    title="Codeforces Blog Scraper API",
    description="API to fetch recent Codeforces blog actions",
    version="1.0.0",
    lifespan=lifespan
)

@app.get("/", include_in_schema=False)
async def root():
    print("Redirecting to /docs")
    return RedirectResponse(url="/docs")

async def update_data():
    global last_update, cached_data, cached_filtered_data
    async with update_lock:
        try:
            data = scrape_recent_actions()  # Get the data
            cached_data = data  # Store in memory
            cached_filtered_data = None  # Invalidate filtered cache when new data arrives
            last_update = datetime.now()
            return True
        except Exception as e:
            print(f"Error updating data: {e}")
            return False

async def ensure_data_exists():
    if not os.path.exists('output'):
        os.makedirs('output')
    if not os.path.exists('output/scrape_recent_actions.json'):
        await update_data()
    else:
        # Load initial data from file
        global cached_data
        with open('output/scrape_recent_actions.json', 'r') as f:
            cached_data = json.load(f)

async def periodic_update():
    while True:
        await asyncio.sleep(1800)  # 30 minutes
        await update_data()

@app.get("/api/recent-actions", 
    response_model=Dict[str, Any],
    summary="Get Recent Codeforces Blog Actions",
    description="Returns the most recent blog actions from Codeforces, including user information and blog details."
)
async def get_recent_actions(filtered: bool = False):
    if cached_data is None:
        raise HTTPException(status_code=503, detail="Data not yet available")
    
    if filtered:
        global cached_filtered_data, is_filtering_in_progress
        if cached_filtered_data is None:
            if is_filtering_in_progress:
                raise HTTPException(status_code=503, detail="Filtering in progress, please try again later")
            # Start filtering in background
            asyncio.create_task(background_filter_content())
            raise HTTPException(status_code=503, detail="Filtering started, please try again in a few seconds")
        return cached_filtered_data
    
    return cached_data

@app.get("/api/health",
    response_model=Dict[str, Optional[str]],
    summary="Health Check",
    description="Returns the current health status of the API and the last update timestamp."
)
async def health_check():
    return {
        "status": "healthy",
        "last_update": last_update.isoformat() if last_update else None
    } 
