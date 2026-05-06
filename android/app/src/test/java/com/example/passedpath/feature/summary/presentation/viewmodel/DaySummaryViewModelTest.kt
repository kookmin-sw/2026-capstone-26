package com.example.passedpath.feature.summary.presentation.viewmodel

import com.example.passedpath.feature.summary.domain.model.DayRouteSummary
import com.example.passedpath.feature.summary.domain.repository.DayRouteSummaryRepository
import com.example.passedpath.feature.summary.domain.usecase.GetDayRouteSummaryUseCase
import com.example.passedpath.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DaySummaryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadSummary exposes summary ui state on success`() = runTest {
        val repository = FakeDayRouteSummaryRepository()
        val viewModel = createViewModel(repository)

        viewModel.loadSummary("2026-04-29")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("2026-04-29", state.dateKey)
        assertFalse(state.isLoading)
        assertEquals(true, state.hasLoaded)
        assertNull(state.errorMessage)
        assertEquals("08:10", state.summary.outingTimeText)
        assertEquals("18:40", state.summary.enterHomeTimeText)
    }

    @Test
    fun `loadSummary keeps default summary state and exposes error on failure`() = runTest {
        val repository = FakeDayRouteSummaryRepository(
            failure = RuntimeException("boom")
        )
        val viewModel = createViewModel(repository)

        viewModel.loadSummary("2026-04-29")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("2026-04-29", state.dateKey)
        assertFalse(state.isLoading)
        assertEquals(false, state.hasLoaded)
        assertEquals("-", state.summary.outingTimeText)
        assertEquals("-", state.summary.enterHomeTimeText)
        assertEquals("0\uBD84", state.summary.totalOutingDurationText)
        assertEquals("0\uD68C", state.summary.totalOutingCountText)
        assertNotNull(state.errorMessage)
    }

    @Test
    fun `loadSummary skips duplicate request for already loaded date`() = runTest {
        val repository = FakeDayRouteSummaryRepository()
        val viewModel = createViewModel(repository)

        viewModel.loadSummary("2026-04-29")
        advanceUntilIdle()
        viewModel.loadSummary("2026-04-29")
        advanceUntilIdle()

        assertEquals(listOf("2026-04-29"), repository.requestedDateKeys)
    }

    @Test
    fun `loadSummary fetches again when date changes`() = runTest {
        val repository = FakeDayRouteSummaryRepository()
        val viewModel = createViewModel(repository)

        viewModel.loadSummary("2026-04-29")
        advanceUntilIdle()
        viewModel.loadSummary("2026-04-30")
        advanceUntilIdle()

        assertEquals(listOf("2026-04-29", "2026-04-30"), repository.requestedDateKeys)
        assertEquals("2026-04-30", viewModel.uiState.value.dateKey)
    }

    private fun createViewModel(
        repository: DayRouteSummaryRepository
    ): DaySummaryViewModel {
        return DaySummaryViewModel(
            getDayRouteSummaryUseCase = GetDayRouteSummaryUseCase(repository)
        )
    }
}

private class FakeDayRouteSummaryRepository(
    private val failure: Throwable? = null
) : DayRouteSummaryRepository {
    val requestedDateKeys = mutableListOf<String>()

    override suspend fun getDayRouteSummary(dateKey: String): DayRouteSummary {
        requestedDateKeys += dateKey
        failure?.let { throw it }
        return DayRouteSummary(
            dateKey = dateKey,
            outingTime = "2026-04-29T08:10:00+09:00",
            enterHomeTime = "2026-04-29T18:40:00+09:00",
            totalOutingCount = 2,
            totalOutingSeconds = 36_120L,
            totalOutingDurationText = "10\uC2DC\uAC04 2\uBD84"
        )
    }
}
