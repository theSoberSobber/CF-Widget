package com.example.codeforces.repository

import com.example.codeforces.model.RecentAction
import com.example.codeforces.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecentActionsRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getRecentActions(): List<RecentAction> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRecentActions()
                val actions = response.recent_actions
                
                // Update each action to include a blogEntry for compatibility with the UI
                actions.map { action ->
                    action.copy(blogEntry = action.toBlogEntry())
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
} 