package com.example.codeforces

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
} 