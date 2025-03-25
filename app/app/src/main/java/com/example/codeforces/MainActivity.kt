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
import com.example.codeforces.ui.theme.CodeforcesBlogWidgetTheme
import com.example.codeforces.viewmodel.RecentActionsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle opening URL if launched from widget
        handleIntent(intent)
        
        setContent {
            CodeforcesBlogWidgetTheme {
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
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            addCategory(Intent.CATEGORY_BROWSABLE)
            addCategory(Intent.CATEGORY_DEFAULT)
        }
        startActivity(intent)
        finish()
    }
} 