package com.example.passedpath.feature.summary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.passedpath.app.AppContainer
import com.example.passedpath.feature.summary.domain.usecase.GetDayRouteSummaryUseCase
import com.example.passedpath.feature.summary.presentation.mapper.toDaySummaryContentUiState
import com.example.passedpath.feature.summary.presentation.state.DaySummaryContentUiState
import com.example.passedpath.feature.summary.presentation.state.DaySummaryUiState
import com.example.passedpath.ui.state.ApiFailureMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DaySummaryViewModel(
    private val getDayRouteSummaryUseCase: GetDayRouteSummaryUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DaySummaryUiState())
    val uiState: StateFlow<DaySummaryUiState> = _uiState.asStateFlow()

    fun loadSummary(dateKey: String, forceRefresh: Boolean = false) {
        val normalizedDateKey = dateKey.trim()
        if (normalizedDateKey.isEmpty()) return

        val currentState = _uiState.value
        if (currentState.isLoading && currentState.dateKey == normalizedDateKey) return
        if (!forceRefresh && currentState.hasLoaded && currentState.dateKey == normalizedDateKey) return

        viewModelScope.launch {
            _uiState.update { state ->
                val isSameDate = state.dateKey == normalizedDateKey
                state.copy(
                    dateKey = normalizedDateKey,
                    summary = if (isSameDate) state.summary else DaySummaryContentUiState.Empty,
                    hasLoaded = if (isSameDate) state.hasLoaded else false,
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                getDayRouteSummaryUseCase(normalizedDateKey)
            }.onSuccess { summary ->
                _uiState.update {
                    it.copy(
                        dateKey = normalizedDateKey,
                        summary = summary.toDaySummaryContentUiState(),
                        hasLoaded = true,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        dateKey = normalizedDateKey,
                        isLoading = false,
                        errorMessage = ApiFailureMessage.fromThrowable(throwable)
                    )
                }
            }
        }
    }
}

class DaySummaryViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DaySummaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DaySummaryViewModel(
                getDayRouteSummaryUseCase = appContainer.getDayRouteSummaryUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
