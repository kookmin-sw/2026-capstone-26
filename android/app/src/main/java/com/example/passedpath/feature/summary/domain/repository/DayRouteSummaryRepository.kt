package com.example.passedpath.feature.summary.domain.repository

import com.example.passedpath.feature.summary.domain.model.DayRouteSummary

interface DayRouteSummaryRepository {
    suspend fun getDayRouteSummary(dateKey: String): DayRouteSummary
}
