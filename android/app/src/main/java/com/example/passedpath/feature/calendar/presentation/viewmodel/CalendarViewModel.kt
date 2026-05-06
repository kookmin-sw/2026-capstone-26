package com.example.passedpath.feature.calendar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.passedpath.app.AppContainer
import com.example.passedpath.feature.calendar.domain.usecase.GetCalendarMonthlyRouteUseCase
import com.example.passedpath.feature.calendar.presentation.model.CalendarDayStatus
import com.example.passedpath.feature.calendar.presentation.state.CalendarUiState
import com.example.passedpath.ui.state.ApiFailureMessage
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalendarViewModel(
    private val getCalendarMonthlyRouteUseCase: GetCalendarMonthlyRouteUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun loadMonth(visibleMonth: YearMonth, forceRefresh: Boolean = false) {
        if (visibleMonth.year !in MinYear..MaxYear) return

        val currentState = _uiState.value
        if (currentState.loadingMonth == visibleMonth) return
        if (!forceRefresh && currentState.loadedMonth == visibleMonth) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loadingMonth = visibleMonth,
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                getCalendarMonthlyRouteUseCase(
                    year = visibleMonth.year,
                    month = visibleMonth.monthValue
                )
            }.onSuccess { monthlyRoute ->
                _uiState.update {
                    it.copy(
                        loadedMonth = visibleMonth,
                        loadingMonth = null,
                        dayStatuses = monthlyRoute.days.associate { day ->
                            day.date to CalendarDayStatus(
                                hasManualData = day.hasManualData,
                                hasLocationData = day.hasLocationData,
                                isBookmarked = day.isBookmarked
                            )
                        },
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        loadingMonth = null,
                        isLoading = false,
                        errorMessage = ApiFailureMessage.fromThrowable(throwable)
                    )
                }
            }
        }
    }
}

class CalendarViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(
                getCalendarMonthlyRouteUseCase = appContainer.getCalendarMonthlyRouteUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

private const val MinYear = 2000
private const val MaxYear = 3000
