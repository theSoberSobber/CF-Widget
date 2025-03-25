package com.example.codeforces.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.codeforces.ui.components.RecentActionItem
import com.example.codeforces.viewmodel.RecentActionsViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentActionsScreen(viewModel: RecentActionsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val isFiltered by viewModel.isFiltered.collectAsState()
    val isRefreshing = uiState is RecentActionsViewModel.UiState.Loading
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filtered mode toggle
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtered Mode",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Switch(
                        checked = isFiltered,
                        onCheckedChange = { viewModel.toggleFilteredMode() }
                    )
                }
                Text(
                    text = "In filtered mode, only selected quality blogs will be shown",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { viewModel.loadRecentActions() },
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (val state = uiState) {
                        is RecentActionsViewModel.UiState.Loading -> {
                            if (!swipeRefreshState.isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        is RecentActionsViewModel.UiState.Success -> {
                            if (state.data.isEmpty()) {
                                EmptyState(
                                    onRetry = { viewModel.loadRecentActions() }
                                )
                            } else {
                                ContentList(
                                    data = state.data,
                                    onItemClick = { url ->
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                        is RecentActionsViewModel.UiState.Error -> {
                            ErrorState(
                                message = state.message,
                                onRetry = { viewModel.loadRecentActions() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentList(
    data: List<com.example.codeforces.model.RecentAction>,
    onItemClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(data) { action ->
            RecentActionItem(
                recentAction = action,
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
private fun EmptyState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        Text(
            text = "No recent actions found",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Refresh")
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        Text(
            text = "Error",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Try Again")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CodeforcesTopAppBar(
    onRefresh: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = { 
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Codeforces ",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Recent Actions",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        actions = {
            Button(
                onClick = onRefresh,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Refresh")
            }
        },
        scrollBehavior = scrollBehavior
    )
} 