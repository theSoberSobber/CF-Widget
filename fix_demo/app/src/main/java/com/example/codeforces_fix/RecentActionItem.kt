package com.example.codeforces_fix

// This is a simplified pseudocode for demonstration purposes
// It shows how the UI component needed to be updated to work with the new data model

// Previous implementation of the RecentActionItem component
fun OldRecentActionItem(recentAction: OldRecentAction) {
    // Skip if no blog entry
    val blogEntry = recentAction.blogEntry ?: return
    
    // Format timestamp
    val timestamp = formatTimestamp(recentAction.timeSeconds)
    
    // Prepare blog URL
    val blogUrl = "https://codeforces.com${blogEntry.url ?: "/blog/entry/${blogEntry.id}"}"
    
    // UI implementation
    Card {
        Column {
            // Author handle from blogEntry
            Text(text = blogEntry.authorHandle)
            
            // Timestamp
            Text(text = timestamp)
            
            // Blog title from blogEntry
            Text(text = blogEntry.title)
            
            // Optional tags if present
            if (blogEntry.tags.isNotEmpty()) {
                // Display tags
            }
        }
    }
}

// Updated implementation of the RecentActionItem component
fun NewRecentActionItem(recentAction: NewRecentAction) {
    // Format timestamp (using system time as the API no longer provides this)
    val timestamp = formatTimestamp(recentAction.timeSeconds)
    
    // Prepare blog URL
    val blogUrl = "https://codeforces.com${recentAction.blog_link}"
    
    // UI implementation
    Card {
        Column {
            // Author handle directly from the recentAction
            Text(text = recentAction.user)
            
            // Timestamp
            Text(text = timestamp)
            
            // Blog title directly from recentAction
            Text(text = recentAction.blog_title)
            
            // No tags in the new model
        }
    }
}

// Helper function for both implementations
fun formatTimestamp(timeSeconds: Long): String {
    // Format the timestamp to a readable string
    return "March 25, 2025"
} 