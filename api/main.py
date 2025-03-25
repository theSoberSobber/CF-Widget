import asyncio
import json
import os
from datetime import datetime
from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException
from fastapi.responses import JSONResponse, RedirectResponse
from scraper import scrape_recent_actions
from typing import Dict, Any, Optional

# Global variables
last_update = None
cached_data = None
update_lock = asyncio.Lock()

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup logic
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
    global last_update, cached_data
    async with update_lock:
        try:
            data = scrape_recent_actions()  # Get the data
            cached_data = data  # Store in memory
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
async def get_recent_actions():
    if cached_data is None:
        raise HTTPException(status_code=503, detail="Data not yet available")
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