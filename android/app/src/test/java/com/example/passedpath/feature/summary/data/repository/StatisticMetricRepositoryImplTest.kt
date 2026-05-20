package com.example.passedpath.feature.summary.data.repository

import com.example.passedpath.feature.summary.data.remote.api.StatisticMetricApi
import com.example.passedpath.feature.summary.data.remote.dto.StatisticMetricAverageDto
import com.example.passedpath.feature.summary.data.remote.dto.StatisticMetricHighlightDto
import com.example.passedpath.feature.summary.data.remote.dto.StatisticMetricResponseDto
import com.example.passedpath.feature.summary.domain.model.StatisticsPeriod
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticMetricRepositoryImplTest {

    @Test
    fun `getTotalOutingSeconds passes period query and maps response`() = runTest {
        val fakeApi = FakeStatisticMetricApi()
        val repository = StatisticMetricRepositoryImpl(statisticMetricApi = fakeApi)

        val result = repository.getTotalOutingSeconds(period = StatisticsPeriod.SIX_MONTHS)

        assertEquals("SIX_MONTHS", fakeApi.requestedPeriod)
        assertEquals(StatisticsPeriod.SIX_MONTHS, result.period)
        assertEquals("2025-12-01", result.startDate)
        assertEquals("2026-05-20", result.endDate)
    }

    @Test(expected = RuntimeException::class)
    fun `getTotalOutingSeconds throws api failures`() = runTest {
        val repository = StatisticMetricRepositoryImpl(
            statisticMetricApi = FakeStatisticMetricApi(
                throwable = RuntimeException("boom")
            )
        )

        repository.getTotalOutingSeconds(period = StatisticsPeriod.WEEK)
    }

    private class FakeStatisticMetricApi(
        private val throwable: Throwable? = null
    ) : StatisticMetricApi {
        var requestedPeriod: String? = null

        override suspend fun getTotalOutingSeconds(period: String?): StatisticMetricResponseDto {
            requestedPeriod = period
            throwable?.let { throw it }
            return StatisticMetricResponseDto(
                metricType = "TOTAL_OUTING_SECONDS",
                period = period,
                startDate = "2025-12-01",
                endDate = "2026-05-20",
                average = StatisticMetricAverageDto(
                    value = 0.0,
                    displayText = "0m",
                    sampleSize = 1
                ),
                bars = emptyList(),
                highlight = StatisticMetricHighlightDto(
                    title = "Title",
                    message = "Message",
                    current = null,
                    previous = null
                )
            )
        }
    }
}
