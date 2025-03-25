package com.example.codeforces_fix

// Previous repository implementation
class OldRecentActionsRepository {
    // Sample method showing the old implementation
    suspend fun getRecentActions(): List<OldRecentAction> {
        // Old implementation expected a response with status and result fields
        val response = fetchFromApi() // This would return OldRecentActionsResponse
        
        return if (response.status == "OK") {
            response.result
        } else {
            emptyList()
        }
    }
    
    private suspend fun fetchFromApi(): OldRecentActionsResponse {
        // This would make an API call and return the response
        // For demo purposes, we're just returning a mock response
        return OldRecentActionsResponse(
            status = "OK", 
            result = emptyList() // This would normally contain data
        )
    }
}

// Updated repository implementation
class NewRecentActionsRepository {
    // Updated method to handle the new API response format
    suspend fun getRecentActions(): List<NewRecentAction> {
        try {
            // New implementation expects a response with recent_actions field
            val response = fetchFromApi() // This would return NewRecentActionsResponse
            val actions = response.recent_actions
            
            // Update each action to include a blogEntry for compatibility with UI
            return actions.map { action ->
                action.copy(blogEntry = action.toBlogEntry())
            }
        } catch (e: Exception) {
            return emptyList()
        }
    }
    
    private suspend fun fetchFromApi(): NewRecentActionsResponse {
        // This would make an API call and return the response
        // For demo purposes, we're just returning a mock response
        return NewRecentActionsResponse(
            recent_actions = listOf(
                NewRecentAction(
                    user = "Serval",
                    user_profile = "/profile/Serval",
                    user_color = "red",
                    blog_title = "Codeforces Round #1011 (Div. 2) Editorial",
                    blog_link = "/blog/entry/140933",
                    img = ImageInfo(
                        alt = "New comment(s)",
                        image = "//codeforces.org/s/21461/images/icons/comment-12x12.png"
                    ),
                    otherImg = emptyList()
                )
            )
        )
    }
} 