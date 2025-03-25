package com.example.codeforces.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.codeforces.model.RecentAction
import com.example.codeforces.repository.RecentActionsRepository
import com.example.codeforces.ui.theme.getCodeforcesUserColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Refresh interval: 30 minutes in milliseconds
private const val AUTO_REFRESH_INTERVAL_MS = 30 * 60 * 1000L

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
    
    companion object {
        fun schedulePeriodicRefresh(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            
            val intent = Intent(context, RefreshReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Cancel any existing alarms
            alarmManager.cancel(pendingIntent)
            
            // Schedule a repeating alarm for refresh
            alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + AUTO_REFRESH_INTERVAL_MS,
                AUTO_REFRESH_INTERVAL_MS,
                pendingIntent
            )
        }
    }
}

@Composable
private fun WidgetContent(
    recentActions: List<RecentAction>,
    lastUpdate: String
) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .padding(12.dp)
        ) {
            // Header with title
            Row(
                modifier = GlanceModifier.fillMaxWidth()
                    .padding(bottom = 8.dp),
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
                modifier = GlanceModifier.padding(bottom = 12.dp)
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
                    items(recentActions) { action ->
                        WidgetBlogItem(action)
                    }
                }
            }
        }
    }
}

@Composable
private fun WidgetBlogItem(action: RecentAction) {
    val blogUrl = "https://codeforces.com${action.blog_link}"
    val userColor = getCodeforcesUserColor(action.user_color)
    
    // Create blog item with rounded corners and shadow
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFFFFFFFF))
            .padding(8.dp)
            .clickable(
                actionStartActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(blogUrl))
                )
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User avatar (placeholder circle)
            Box(
                modifier = GlanceModifier
                    .size(32.dp)
                    .background(userColor)
            ) {
                Text(
                    text = action.user.firstOrNull()?.uppercase() ?: "U",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.padding(8.dp)
                )
            }
            
            // Text content
            Column(
                modifier = GlanceModifier
                    .defaultWeight()
                    .padding(start = 8.dp)
            ) {
                // Blog title
                Text(
                    text = action.blog_title,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
                
                // Username with color
                Text(
                    text = "by ${action.user}",
                    style = TextStyle(
                        fontSize = 12.sp
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

class CodeforcesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CodeforcesWidget()
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        // Schedule periodic refresh when widget is added
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            CodeforcesWidget.schedulePeriodicRefresh(context)
        }
    }
}

// Receiver for the automatic refresh
class RefreshReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CodeforcesWidget()
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        // Trigger refresh directly
        refreshWidgets(context)
    }
    
    private fun refreshWidgets(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
            val glanceIds = glanceAppWidgetManager.getGlanceIds(CodeforcesWidget::class.java)
            
            for (glanceId in glanceIds) {
                refreshWidget(context, glanceId)
            }
        }
    }
    
    private suspend fun refreshWidget(context: Context, glanceId: GlanceId) {
        // Update "Refreshing..." status
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[PreferencesKeys.LAST_UPDATE] = "Refreshing..."
            }
        }
        CodeforcesWidget().updateAll(context)
        
        // Fetch data
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