package com.example.passedpath.feature.summary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.passedpath.app.AppContainer
import com.example.passedpath.feature.summary.domain.usecase.GetTotalOutingSecondsStatisticsUseCase
import com.example.passedpath.feature.summary.presentation.mapper.toStatisticsPeriod
import com.example.passedpath.feature.summary.presentation.mapper.toSummaryDetailPeriod
import com.example.passedpath.feature.summary.presentation.mapper.toTotalOutingDurationSummaryDetailUiState
import com.example.passedpath.feature.summary.presentation.state.SummaryDetailPeriod
import com.example.passedpath.feature.summary.presentation.state.SummaryDetailViewModelUiState
import com.example.passedpath.ui.state.ApiFailureMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SummaryDetailViewModel(
    private val getTotalOutingSecondsStatisticsUseCase: GetTotalOutingSecondsStatisticsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SummaryDetailViewModelUiState())
    val uiState: StateFlow<SummaryDetailViewModelUiState> = _uiState.asStateFlow()

    fun loadTotalOutingDuration(forceRefresh: Boolean = false) {
        val currentState = _uiState.value
        if (currentState.isLoading) return
        if (!forceRefresh && currentState.hasLoaded) return

        fetchTotalOutingDuration(period = currentState.selectedPeriod)
    }

    fun selectPeriod(period: SummaryDetailPeriod) {
        val currentState = _uiState.value
        if (currentState.selectedPeriod == period && currentState.hasLoaded) return
        if (currentState.isLoading && currentState.selectedPeriod == period) return

        _uiState.update { state ->
            state.copy(selectedPeriod = period)
        }
        fetchTotalOutingDuration(period = period)
    }

    private fun fetchTotalOutingDuration(period: SummaryDetailPeriod) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    selectedPeriod = period,
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                getTotalOutingSecondsStatisticsUseCase(period = period.toStatisticsPeriod())
            }.onSuccess { statisticMetric ->
                _uiState.update {
                    it.copy(
                        selectedPeriod = statisticMetric.period.toSummaryDetailPeriod(),
                        content = statisticMetric.toTotalOutingDurationSummaryDetailUiState(),
                        hasLoaded = true,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ApiFailureMessage.fromThrowable(throwable)
                    )
                }
            }
        }
    }
}

class SummaryDetailViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SummaryDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SummaryDetailViewModel(
                getTotalOutingSecondsStatisticsUseCase = appContainer.getTotalOutingSecondsStatisticsUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
