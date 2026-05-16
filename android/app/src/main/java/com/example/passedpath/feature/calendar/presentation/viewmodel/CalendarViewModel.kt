package com.example.passedpath.feature.calendar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.passedpath.app.AppContainer
import com.example.passedpath.feature.bookmark.domain.usecase.MarkDayRouteBookmarksUseCase
import com.example.passedpath.feature.calendar.domain.usecase.GetCalendarMonthlyRouteUseCase
import com.example.passedpath.feature.calendar.presentation.model.CalendarDayStatus
import com.example.passedpath.feature.calendar.presentation.state.CalendarUiState
import com.example.passedpath.ui.state.ApiFailureMessage
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalendarViewModel(
    private val getCalendarMonthlyRouteUseCase: GetCalendarMonthlyRouteUseCase,
    private val markDayRouteBookmarksUseCase: MarkDayRouteBookmarksUseCase
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

    fun markSelectedDatesAsBookmarks(dateKeys: List<String>) {
        val targetDateKeys = dateKeys.distinct().sorted()
        if (targetDateKeys.isEmpty()) return
        if (_uiState.value.bookmarkRegistrationUiState.isSubmitting) return

        _uiState.update { currentState ->
            currentState.copy(
                bookmarkRegistrationUiState = currentState.bookmarkRegistrationUiState.copy(
                    isSubmitting = true,
                    feedbackMessage = null,
                    isSuccess = false
                )
            )
        }

        viewModelScope.launch {
            runCatching {
                markDayRouteBookmarksUseCase(targetDateKeys)
            }.onSuccess {
                val bookmarkedDates = targetDateKeys.mapNotNull { dateKey ->
                    runCatching { LocalDate.parse(dateKey) }.getOrNull()
                }
                _uiState.update { currentState ->
                    val patchedStatuses = currentState.dayStatuses.toMutableMap()
                    bookmarkedDates.forEach { date ->
                        val currentStatus = patchedStatuses[date] ?: CalendarDayStatus()
                        patchedStatuses[date] = currentStatus.copy(isBookmarked = true)
                    }
                    currentState.copy(
                        dayStatuses = patchedStatuses,
                        bookmarkRegistrationUiState = currentState.bookmarkRegistrationUiState.copy(
                            isSubmitting = false,
                            feedbackMessage = CalendarBookmarkRegistrationSuccessMessage,
                            feedbackEventId = currentState.bookmarkRegistrationUiState
                                .feedbackEventId + 1,
                            isSuccess = true
                        )
                    )
                }
            }.onFailure { throwable ->
                _uiState.update { currentState ->
                    currentState.copy(
                        bookmarkRegistrationUiState = currentState.bookmarkRegistrationUiState.copy(
                            isSubmitting = false,
                            feedbackMessage = ApiFailureMessage.fromThrowable(throwable),
                            feedbackEventId = currentState.bookmarkRegistrationUiState
                                .feedbackEventId + 1,
                            isSuccess = false
                        )
                    )
                }
            }
        }
    }

    fun consumeBookmarkRegistrationFeedback(eventId: Long) {
        _uiState.update { currentState ->
            if (currentState.bookmarkRegistrationUiState.feedbackEventId == eventId) {
                currentState.copy(
                    bookmarkRegistrationUiState = currentState.bookmarkRegistrationUiState.copy(
                        feedbackMessage = null
                    )
                )
            } else {
                currentState
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
                getCalendarMonthlyRouteUseCase = appContainer.getCalendarMonthlyRouteUseCase,
                markDayRouteBookmarksUseCase = appContainer.markDayRouteBookmarksUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

private const val MinYear = 2000
private const val MaxYear = 3000
private const val CalendarBookmarkRegistrationSuccessMessage =
    "선택한 날짜를 즐겨찾기로 등록했어요"
