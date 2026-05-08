package com.example.passedpath.feature.summary.domain.usecase

import com.example.passedpath.feature.summary.domain.model.DayRouteSummary
import com.example.passedpath.feature.summary.domain.repository.DayRouteSummaryRepository

class GetDayRouteSummaryUseCase(
    private val dayRouteSummaryRepository: DayRouteSummaryRepository
) {
    suspend operator fun invoke(dateKey: String): DayRouteSummary {
        return dayRouteSummaryRepository.getDayRouteSummary(dateKey = dateKey)
    }
}
