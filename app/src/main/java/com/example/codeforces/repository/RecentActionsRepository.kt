package com.example.codeforces.repository

import com.example.codeforces.model.RecentAction
import com.example.codeforces.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecentActionsRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getRecentActions(): Result<List<RecentAction>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRecentActions()
                Result.success(response.recent_actions)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
} 