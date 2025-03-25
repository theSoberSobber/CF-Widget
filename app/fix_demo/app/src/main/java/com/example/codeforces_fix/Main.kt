package com.example.codeforces_fix

import kotlin.concurrent.thread

/**
 * This file demonstrates the complete fix for the Codeforces Recent Actions app.
 * It shows both the old approach (which fails with the new API) and the new approach that works.
 * Note: This is simplified code for demonstration only.
 */

fun main() {
    println("Codeforces Recent Actions API Fix Demo")
    println("--------------------------------------")
    
    // Demonstrate old approach (will fail with new API)
    println("\n1. Old approach with old API format (would work):")
    simulateOldApiOldImplementation()
    
    println("\n2. Old approach with new API format (would fail):")
    simulateNewApiOldImplementation()
    
    println("\n3. New approach with new API format (fixed version):")
    simulateNewApiNewImplementation()
}

// Simulation of old approach with old API format
fun simulateOldApiOldImplementation() {
    // Create mock old-format data
    val oldData = OldRecentActionsResponse(
        status = "OK",
        result = listOf(
            OldRecentAction(
                timeSeconds = 1615123456,
                blogEntry = OldBlogEntry(
                    id = 123456,
                    originalLocale = "en",
                    creationTimeSeconds = 1615123456,
                    authorHandle = "user123",
                    title = "Sample Blog Title",
                    content = "Sample content",
                    locale = "en",
                    modificationTimeSeconds = 1615123456,
                    allowViewHistory = false,
                    tags = listOf("tag1", "tag2"),
                    rating = 0,
                    url = "/blog/entry/123456"
                )
            )
        )
    )
    
    // Process with old implementation
    val repository = object {
        fun getRecentActions(): List<OldRecentAction> {
            return if (oldData.status == "OK") {
                oldData.result
            } else {
                emptyList()
            }
        }
    }
    
    // Simulate display
    val actions = repository.getRecentActions()
    if (actions.isNotEmpty()) {
        println("✅ SUCCESS: Found ${actions.size} recent actions")
        println("First action: ${actions[0].blogEntry?.title} by ${actions[0].blogEntry?.authorHandle}")
    } else {
        println("❌ ERROR: No recent actions found")
    }
}

// Simulation of old approach with new API format
fun simulateNewApiOldImplementation() {
    // Create mock new-format data but try to process with old implementation
    val newData = """
        {
            "recent_actions": [
                {
                    "user": "Serval",
                    "user_profile": "/profile/Serval",
                    "user_color": "red",
                    "blog_title": "Codeforces Round #1011 (Div. 2) Editorial",
                    "blog_link": "/blog/entry/140933"
                }
            ]
        }
    """.trimIndent()
    
    println("Received API data: $newData")
    println("Attempting to process with old implementation...")
    println("Expecting 'status' and 'result' fields, but got 'recent_actions'")
    println("❌ ERROR: No recent actions found (parsing failed)")
}

// Simulation of new approach with new API format
fun simulateNewApiNewImplementation() {
    // Create mock new-format data
    val newData = NewRecentActionsResponse(
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
    
    // Process with new implementation
    val repository = object {
        fun getRecentActions(): List<NewRecentAction> {
            // Simply retrieve recent_actions without checking status
            val actions = newData.recent_actions
            
            // Add backward compatibility
            return actions.map { action ->
                action.copy(blogEntry = action.toBlogEntry())
            }
        }
    }
    
    // Simulate display
    val actions = repository.getRecentActions()
    if (actions.isNotEmpty()) {
        println("✅ SUCCESS: Found ${actions.size} recent actions")
        println("First action: ${actions[0].blog_title} by ${actions[0].user}")
        println("(With backward compatibility for old UI: ${actions[0].blogEntry?.title})")
    } else {
        println("❌ ERROR: No recent actions found")
    }
} 