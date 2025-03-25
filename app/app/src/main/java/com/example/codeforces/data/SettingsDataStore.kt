package com.example.codeforces.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    private val FILTERED_MODE = booleanPreferencesKey("filtered_mode")

    val isFilteredMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[FILTERED_MODE] ?: false
        }

    suspend fun setFilteredMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[FILTERED_MODE] = enabled
        }
    }
} 