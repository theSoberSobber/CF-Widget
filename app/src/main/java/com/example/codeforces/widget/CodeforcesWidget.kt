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
import androidx.glance.*
import androidx.glance.action.*
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.*
import androidx.glance.appwidget.lazy.*
import androidx.glance.appwidget.state.*
import androidx.glance.color.*
import androidx.glance.layout.*
import androidx.glance.state.*
import androidx.glance.text.*
import com.example.codeforces.R
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
            .background(ColorProvider(day = Color(0xFFF5F5F5), night = Color(0xFF121212)))
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
private fun WidgetBlogItem(blog: RecentAction) {
    val userColor = getCodeforcesUserColor(blog.user_color)
    val blogUrl = "https://codeforces.com${blog.blog_link}"
    
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(ColorProvider(day = Color(0xFFFFFFFF), night = Color(0xFF121212)))
            .padding(8.dp)
            .clickable(
                actionStartActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(blogUrl))
                )
            )
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Use PNG images based on alt text
            blog.img?.let { imageInfo ->
                when (imageInfo.alt) {
                    "Necropost" -> Image(
                        provider = ImageProvider(R.drawable.necropost),
                        contentDescription = "Necropost",
                        modifier = GlanceModifier.size(24.dp)
                    )
                    "Text created or updated" -> Image(
                        provider = ImageProvider(R.drawable.text_created_or_updated),
                        contentDescription = "Text created or updated",
                        modifier = GlanceModifier.size(24.dp)
                    )
                    "New comment(s)" -> Image(
                        provider = ImageProvider(R.drawable.new_comments),
                        contentDescription = "New comment(s)",
                        modifier = GlanceModifier.size(24.dp)
                    )
                    else -> Text(
                        text = imageInfo.alt,
                        style = TextStyle(
                            color = ColorProvider(day = Color(0xFF000000), night = Color(0xFFFFFFFF)),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = GlanceModifier.padding(end = 8.dp)
                    )
                }
            } ?: Text(
                text = "no-img",
                style = TextStyle(
                    color = ColorProvider(day = Color(0xFF000000), night = Color(0xFFFFFFFF)),
                    fontSize = 12.sp
                ),
                modifier = GlanceModifier.padding(end = 8.dp)
            )
            
            Column(
                modifier = GlanceModifier
                    .defaultWeight()
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = blog.blog_title,
                    style = TextStyle(
                        color = ColorProvider(day = Color(0xFF000000), night = Color(0xFFFFFFFF)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 2
                )
                Row {
                    Text(
                        text = "by ",
                        style = TextStyle(
                            color = ColorProvider(day = Color(0xFF666666), night = Color(0xFFAAAAAA)),
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = blog.user,
                        style = TextStyle(
                            color = ColorProvider(day = userColor, night = userColor),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
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