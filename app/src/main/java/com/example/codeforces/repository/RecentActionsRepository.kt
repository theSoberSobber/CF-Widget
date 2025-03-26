package com.example.codeforces.repository

import android.util.Log
import com.example.codeforces.model.RecentAction
import com.example.codeforces.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecentActionsRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getRecentActions(filtered: Boolean = false): List<RecentAction> {
        val url = if (filtered) "?filtered=true" else ""
        Log.d("RecentActionsRepository", "Fetching recent actions with URL: recentActions$url")
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRecentActions(filtered)
                val actions = response.recent_actions
                Log.d("RecentActionsRepository", "Successfully fetched ${actions.size} actions with filtered=$filtered")
                
                // Update each action to include a blogEntry for compatibility with the UI
                actions.map { action ->
                    action.copy(blogEntry = action.toBlogEntry())
                }
            } catch (e: Exception) {
                Log.e("RecentActionsRepository", "Error fetching recent actions with filtered=$filtered", e)
                emptyList()
            }
        }
    }
} 