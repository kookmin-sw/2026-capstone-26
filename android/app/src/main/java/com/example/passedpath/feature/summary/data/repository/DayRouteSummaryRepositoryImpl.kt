package com.example.passedpath.feature.summary.data.repository

import com.example.passedpath.feature.summary.data.remote.api.DayRouteSummaryApi
import com.example.passedpath.feature.summary.data.remote.mapper.toDayRouteSummary
import com.example.passedpath.feature.summary.domain.model.DayRouteSummary
import com.example.passedpath.feature.summary.domain.repository.DayRouteSummaryRepository

class DayRouteSummaryRepositoryImpl(
    private val dayRouteSummaryApi: DayRouteSummaryApi
) : DayRouteSummaryRepository {
    override suspend fun getDayRouteSummary(dateKey: String): DayRouteSummary {
        return dayRouteSummaryApi.getDayRouteSummary(date = dateKey)
            .toDayRouteSummary(dateKey = dateKey)
    }
}
