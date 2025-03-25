package com.example.codeforces_fix

// Previous API Service interface
interface OldApiService {
    // Old endpoint expecting a response with status and result fields
    suspend fun getRecentActions(): OldRecentActionsResponse
}

// Updated API Service interface
interface NewApiService {
    // Same endpoint but now expecting a response with recent_actions field
    suspend fun getRecentActions(): NewRecentActionsResponse
}

// The Retrofit setup would remain the same
object RetrofitClient {
    private const val BASE_URL = "https://cfdata.pavit.xyz/"

    // Create and configure Retrofit client
    private val retrofit = Any() // This would be a Retrofit instance

    // Create API service
    val apiService: NewApiService = Any() as NewApiService // This would use retrofit.create()
} 