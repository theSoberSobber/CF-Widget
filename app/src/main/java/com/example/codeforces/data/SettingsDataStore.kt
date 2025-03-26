package com.example.codeforces.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    private val FILTERED_MODE = booleanPreferencesKey("filtered_mode")
    private val COLOR_FILTER = stringPreferencesKey("color_filter")
    private val SHOW_UNRATED = booleanPreferencesKey("show_unrated")

    val isFilteredMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[FILTERED_MODE] ?: false
        }

    val colorFilter: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[COLOR_FILTER]
        }

    val showUnrated: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SHOW_UNRATED] ?: false
        }

    suspend fun setFilteredMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[FILTERED_MODE] = enabled
        }
    }

    suspend fun setColorFilter(color: String?) {
        context.dataStore.edit { preferences ->
            if (color != null) {
                preferences[COLOR_FILTER] = color
            } else {
                preferences.remove(COLOR_FILTER)
            }
        }
    }

    suspend fun setShowUnrated(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_UNRATED] = enabled
        }
    }
} 