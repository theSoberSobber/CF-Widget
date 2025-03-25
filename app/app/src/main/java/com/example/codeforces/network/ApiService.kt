package com.example.codeforces.network

import com.example.codeforces.model.RecentActionsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/recent-actions")
    suspend fun getRecentActions(@Query("filtered") filtered: Boolean = false): RecentActionsResponse
} 