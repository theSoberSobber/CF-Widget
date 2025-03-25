package com.example.codeforces.network

import com.example.codeforces.model.RecentActionsResponse
import retrofit2.http.GET

interface ApiService {
    @GET("api/recent-actions")
    suspend fun getRecentActions(): RecentActionsResponse
} 