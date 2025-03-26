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

    private val _selectedColor = MutableStateFlow<String?>(null)
    val selectedColor: StateFlow<String?> = _selectedColor.asStateFlow()

    private val _showUnrated = MutableStateFlow(false)
    val showUnrated: StateFlow<Boolean> = _showUnrated.asStateFlow()

    init {
        viewModelScope.launch {
            settingsDataStore.isFilteredMode.collect { isFiltered ->
                _isFiltered.value = isFiltered
                loadRecentActions()
            }
        }
        viewModelScope.launch {
            settingsDataStore.colorFilter.collect { color ->
                _selectedColor.value = color
                loadRecentActions()
            }
        }
        viewModelScope.launch {
            settingsDataStore.showUnrated.collect { showUnrated ->
                _showUnrated.value = showUnrated
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

    fun setColorFilter(color: String?) {
        viewModelScope.launch {
            settingsDataStore.setColorFilter(color)
            _selectedColor.value = color
            loadRecentActions()
        }
    }

    fun toggleShowUnrated() {
        viewModelScope.launch {
            val newValue = !_showUnrated.value
            settingsDataStore.setShowUnrated(newValue)
            _showUnrated.value = newValue
            loadRecentActions()
        }
    }

    fun loadRecentActions() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val actions = repository.getRecentActions(_isFiltered.value)
                val filteredActions = if (_selectedColor.value != null) {
                    filterActionsByColor(actions, _selectedColor.value!!)
                } else {
                    actions
                }
                if (filteredActions.isNotEmpty()) {
                    _uiState.value = UiState.Success(filteredActions)
                } else {
                    _uiState.value = UiState.Error("No recent actions found")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun filterActionsByColor(actions: List<RecentAction>, selectedColor: String): List<RecentAction> {
        val colorHierarchy = listOf("gray", "green", "cyan", "blue", "violet", "yellow")
        val selectedIndex = colorHierarchy.indexOf(selectedColor.lowercase())
        if (selectedIndex == -1) return actions // If color not in hierarchy, show all

        return actions.filter { action ->
            val actionColor = action.user_color.lowercase()
            // Handle unrated/announcement content
            if (actionColor == "admin" || actionColor == "black") {
                _showUnrated.value
            } else {
                // If color is not in hierarchy (above our hierarchy), show it
                if (!colorHierarchy.contains(actionColor)) {
                    true
                } else {
                    val actionIndex = colorHierarchy.indexOf(actionColor)
                    actionIndex >= selectedIndex // Include the selected color and above
                }
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val data: List<RecentAction>) : UiState()
        data class Error(val message: String) : UiState()
    }
} 