package com.example.codeforces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeforces.model.RecentAction
import com.example.codeforces.repository.RecentActionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecentActionsViewModel : ViewModel() {
    private val repository = RecentActionsRepository()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadRecentActions()
    }

    fun loadRecentActions() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getRecentActions()
                .onSuccess { actions ->
                    _uiState.value = UiState.Success(actions)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val data: List<RecentAction>) : UiState()
        data class Error(val message: String) : UiState()
    }
} 