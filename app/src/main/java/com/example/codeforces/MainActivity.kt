package com.example.codeforces

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codeforces.ui.screens.RecentActionsScreen
import com.example.codeforces.ui.theme.CodeforcesRecentActionsTheme
import com.example.codeforces.viewmodel.RecentActionsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle opening URL if launched from widget
        handleIntent(intent)
        
        setContent {
            CodeforcesRecentActionsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: RecentActionsViewModel = viewModel()
                    RecentActionsScreen(viewModel)
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    
    private fun handleIntent(intent: Intent) {
        // Check if launched from widget with URL
        intent.getStringExtra("url")?.let { url ->
            openBlogPost(url)
        }
        
        // Handle direct URL intents
        if (Intent.ACTION_VIEW == intent.action) {
            intent.data?.let { uri ->
                // If it's a Codeforces URL, handle it
                if (uri.host == "codeforces.com") {
                    openBlogPost(uri.toString())
                }
            }
        }
    }
    
    private fun openBlogPost(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
} 