from botasaurus.request import request, Request
from botasaurus.soupify import soupify

@request
def scrape_recent_actions(request: Request, data):
    # Visit the website
    response = request.get("https://codeforces.com/")

    # Create a BeautifulSoup object    
    soup = soupify(response)
    
    # Find the recent actions container
    recent_actions_div = soup.find("div", class_="recent-actions")
    
    actions = []
    
    if recent_actions_div:
        for li in recent_actions_div.find_all("li"):
            user = li.find("a", class_="rated-user")
            blog = li.find_all("a")[1] if len(li.find_all("a")) > 1 else None
            images = li.find_all("img")

            # Extract user color from class (e.g., "user-blue", "user-cyan")
            user_color = None
            if user:
                for class_name in user.get("class", []):
                    if class_name.startswith("user-"):
                        user_color = class_name.replace("user-", "")
                        break  # Stop at first match

            # Extract the first image (img) and any others (otherImg) as objects
            img = {
                "alt": images[0]["alt"] if images and images[0].has_attr("alt") else None,
                "image": images[0]["src"] if images else None
            } if images else None

            other_img = [
                {
                    "alt": img["alt"] if img.has_attr("alt") else None,
                    "image": img["src"]
                } for img in images[1:] if img.has_attr("src")
            ] if len(images) > 1 else []

            if user and blog:
                actions.append({
                    "user": user.get_text(strip=True),
                    "user_profile": user["href"],
                    "user_color": user_color,  # New field
                    "blog_title": blog.get_text(strip=True),
                    "blog_link": blog["href"],
                    "img": img,
                    "otherImg": other_img
                })

    return {"recent_actions": actions}

# Initiate the web scraping task
data = scrape_recent_actions()
