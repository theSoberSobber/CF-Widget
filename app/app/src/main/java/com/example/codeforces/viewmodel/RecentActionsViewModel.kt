package com.example.codeforces.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeforces.data.SettingsDataStore
import com.example.codeforces.model.RecentAction
import com.example.codeforces.repository.RecentActionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecentActionsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RecentActionsRepository()
    private val settingsDataStore = SettingsDataStore(application)

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isFiltered = MutableStateFlow(false)
    val isFiltered: StateFlow<Boolean> = _isFiltered.asStateFlow()

    init {
        viewModelScope.launch {
            settingsDataStore.isFilteredMode.collect { isFiltered ->
                _isFiltered.value = isFiltered
                loadRecentActions()
            }
        }
    }

    fun toggleFilteredMode() {
        viewModelScope.launch {
            val newValue = !_isFiltered.value
            settingsDataStore.setFilteredMode(newValue)
            _isFiltered.value = newValue
            loadRecentActions()
        }
    }

    fun loadRecentActions() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val actions = repository.getRecentActions(_isFiltered.value)
                if (actions.isNotEmpty()) {
                    _uiState.value = UiState.Success(actions)
                } else {
                    _uiState.value = UiState.Error("No recent actions found")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val data: List<RecentAction>) : UiState()
        data class Error(val message: String) : UiState()
    }
} 