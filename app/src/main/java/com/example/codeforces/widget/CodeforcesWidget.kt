package com.example.codeforces.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.codeforces.MainActivity
import com.example.codeforces.model.RecentAction
import com.example.codeforces.repository.RecentActionsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object PreferencesKeys {
    val RECENT_ACTIONS = stringPreferencesKey("recent_actions")
    val LAST_UPDATE = stringPreferencesKey("last_update")
}

class CodeforcesWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val recentActionsJson = prefs[PreferencesKeys.RECENT_ACTIONS] ?: ""
            val lastUpdate = prefs[PreferencesKeys.LAST_UPDATE] ?: "Never updated"

            val recentActions = if (recentActionsJson.isNotEmpty()) {
                try {
                    Json.decodeFromString<List<RecentAction>>(recentActionsJson)
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }

            WidgetContent(
                recentActions = recentActions,
                lastUpdate = lastUpdate
            )
        }
    }
}

@Composable
private fun WidgetContent(
    recentActions: List<RecentAction>,
    lastUpdate: String
) {
    Column(
        modifier = GlanceModifier.fillMaxSize()
            .padding(8.dp)
    ) {
        // Header with title
        Row(
            modifier = GlanceModifier.fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CF Blogs",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = GlanceModifier.defaultWeight()
            )
            
            // Refresh button
            Text(
                text = "Refresh",
                style = TextStyle(
                    fontSize = 12.sp
                ),
                modifier = GlanceModifier
                    .clickable(actionRunCallback<RefreshAction>())
                    .padding(4.dp)
            )
        }
        
        // Last update time
        Text(
            text = "Last updated: $lastUpdate",
            style = TextStyle(
                fontSize = 12.sp
            ),
            modifier = GlanceModifier.padding(8.dp)
        )
        
        // Content - Recent actions list
        if (recentActions.isEmpty()) {
            // Empty state
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "No recent actions found",
                    style = TextStyle(
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = GlanceModifier.height(8.dp))
                Text(
                    text = "Refresh",
                    style = TextStyle(
                        fontSize = 14.sp
                    ),
                    modifier = GlanceModifier
                        .clickable(actionRunCallback<RefreshAction>())
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else {
            // List of recent actions
            LazyColumn {
                items(recentActions.filter { it.blogEntry != null }) { action ->
                    WidgetBlogItem(action)
                }
            }
        }
    }
}

@Composable
private fun WidgetBlogItem(action: RecentAction) {
    val blogEntry = action.blogEntry ?: return
    val blogUrl = "https://codeforces.com${blogEntry.url ?: "/blog/entry/${blogEntry.id}"}"
    
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .padding(8.dp)
            .clickable(
                actionStartActivity<MainActivity>(
                    actionParametersOf(
                        ActionParameters.Key<String>("url") to blogUrl
                    )
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Text content
        Column(
            modifier = GlanceModifier.defaultWeight()
        ) {
            // Blog title or action text
            Text(
                text = blogEntry.title,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
            
            // Username
            Text(
                text = "by ${blogEntry.authorHandle}",
                style = TextStyle(
                    fontSize = 12.sp
                ),
                maxLines = 1
            )
        }
    }
}

class CodeforcesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CodeforcesWidget()
}

class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[PreferencesKeys.LAST_UPDATE] = "Refreshing..."
            }
        }
        CodeforcesWidget().updateAll(context)

        // Fetch new data
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = RecentActionsRepository()
                val result = repository.getRecentActions()
                
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                    prefs.toMutablePreferences().apply {
                        // Store recent actions as JSON
                        this[PreferencesKeys.RECENT_ACTIONS] = Json.encodeToString(result)
                        // Update last refresh timestamp
                        val timestamp = System.currentTimeMillis()
                        val formattedTime = android.text.format.DateFormat
                            .getTimeFormat(context)
                            .format(timestamp)
                        this[PreferencesKeys.LAST_UPDATE] = formattedTime
                    }
                }
                CodeforcesWidget().updateAll(context)
            } catch (e: Exception) {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                    prefs.toMutablePreferences().apply {
                        this[PreferencesKeys.LAST_UPDATE] = "Error: ${e.message}"
                    }
                }
                CodeforcesWidget().updateAll(context)
            }
        }
    }
} 