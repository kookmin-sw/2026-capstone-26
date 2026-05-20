package com.example.passedpath.feature.summary.presentation.viewmodel

import com.example.passedpath.feature.summary.domain.model.HighlightMetricValue
import com.example.passedpath.feature.summary.domain.model.StatisticMetric
import com.example.passedpath.feature.summary.domain.model.StatisticMetricAverage
import com.example.passedpath.feature.summary.domain.model.StatisticMetricHighlight
import com.example.passedpath.feature.summary.domain.repository.StatisticMetricRepository
import com.example.passedpath.feature.summary.domain.usecase.GetTotalOutingSecondsStatisticsUseCase
import com.example.passedpath.feature.summary.presentation.state.SummaryDetailMetric
import com.example.passedpath.feature.summary.presentation.state.SummaryDetailPeriod
import com.example.passedpath.feature.summary.domain.model.StatisticMetricBarItem
import com.example.passedpath.feature.summary.domain.model.StatisticsPeriod
import com.example.passedpath.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SummaryDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadTotalOutingDuration requests week and exposes ui state on success`() = runTest {
        val repository = FakeStatisticMetricRepository()
        val viewModel = createViewModel(repository)

        viewModel.loadTotalOutingDuration()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(listOf(StatisticsPeriod.WEEK), repository.requestedPeriods)
        assertTrue(state.hasLoaded)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(SummaryDetailPeriod.WEEK, state.selectedPeriod)
        assertEquals(SummaryDetailMetric.TOTAL_OUTING_DURATION, state.content?.metric)
    }

    @Test
    fun `selectPeriod fetches selected period`() = runTest {
        val repository = FakeStatisticMetricRepository()
        val viewModel = createViewModel(repository)

        viewModel.selectPeriod(SummaryDetailPeriod.MONTH)
        advanceUntilIdle()

        assertEquals(listOf(StatisticsPeriod.MONTH), repository.requestedPeriods)
        assertEquals(SummaryDetailPeriod.MONTH, viewModel.uiState.value.selectedPeriod)
    }

    @Test
    fun `loadTotalOutingDuration forceRefresh requests again`() = runTest {
        val repository = FakeStatisticMetricRepository()
        val viewModel = createViewModel(repository)

        viewModel.loadTotalOutingDuration()
        advanceUntilIdle()
        viewModel.loadTotalOutingDuration(forceRefresh = true)
        advanceUntilIdle()

        assertEquals(
            listOf(StatisticsPeriod.WEEK, StatisticsPeriod.WEEK),
            repository.requestedPeriods
        )
    }

    @Test
    fun `loadTotalOutingDuration exposes error on failure`() = runTest {
        val repository = FakeStatisticMetricRepository(
            failure = RuntimeException("boom")
        )
        val viewModel = createViewModel(repository)

        viewModel.loadTotalOutingDuration()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.hasLoaded)
        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage)
    }

    private fun createViewModel(
        repository: StatisticMetricRepository
    ): SummaryDetailViewModel {
        return SummaryDetailViewModel(
            getTotalOutingSecondsStatisticsUseCase = GetTotalOutingSecondsStatisticsUseCase(repository)
        )
    }
}

private class FakeStatisticMetricRepository(
    private val failure: Throwable? = null
) : StatisticMetricRepository {
    val requestedPeriods = mutableListOf<StatisticsPeriod>()

    override suspend fun getTotalOutingSeconds(period: StatisticsPeriod): StatisticMetric {
        requestedPeriods += period
        failure?.let { throw it }
        return StatisticMetric(
            metricType = "TOTAL_OUTING_SECONDS",
            period = period,
            startDate = "2026-05-14",
            endDate = "2026-05-20",
            average = StatisticMetricAverage(
                value = 3600.0,
                displayText = "1h",
                sampleSize = 1
            ),
            bars = listOf(
                StatisticMetricBarItem(
                    label = "Mon",
                    startDate = "2026-05-20",
                    endDate = "2026-05-20",
                    value = 3600.0,
                    displayText = "1h",
                    hasValue = true,
                    sampleSize = 1
                )
            ),
            highlight = StatisticMetricHighlight(
                title = "Highlight",
                message = "Message",
                current = HighlightMetricValue("Current", 3600.0, "1h", 1),
                previous = HighlightMetricValue("Previous", null, null, 0)
            )
        )
    }
}
