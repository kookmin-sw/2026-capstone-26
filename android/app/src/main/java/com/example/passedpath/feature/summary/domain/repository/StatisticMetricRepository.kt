package com.example.passedpath.feature.summary.domain.repository

import com.example.passedpath.feature.summary.domain.model.StatisticMetric
import com.example.passedpath.feature.summary.domain.model.StatisticsPeriod

interface StatisticMetricRepository {
    suspend fun getTotalOutingSeconds(period: StatisticsPeriod): StatisticMetric
}
