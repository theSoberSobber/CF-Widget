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
                if (response.status == "OK") {
                    response.result
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
} 