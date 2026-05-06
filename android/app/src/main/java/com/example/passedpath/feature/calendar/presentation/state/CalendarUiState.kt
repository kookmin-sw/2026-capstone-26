package com.example.passedpath.feature.calendar.presentation.state

import com.example.passedpath.feature.calendar.presentation.model.CalendarDayStatus
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val loadedMonth: YearMonth? = null,
    val loadingMonth: YearMonth? = null,
    val dayStatuses: Map<LocalDate, CalendarDayStatus> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
