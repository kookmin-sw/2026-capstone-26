package com.example.passedpath.feature.calendar.presentation.viewmodel

import com.example.passedpath.feature.calendar.domain.model.CalendarMonthlyDay
import com.example.passedpath.feature.calendar.domain.model.CalendarMonthlyRoute
import com.example.passedpath.feature.calendar.domain.repository.CalendarMonthlyRouteRepository
import com.example.passedpath.feature.calendar.domain.usecase.GetCalendarMonthlyRouteUseCase
import com.example.passedpath.testutil.MainDispatcherRule
import java.time.LocalDate
import java.time.YearMonth
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
class CalendarViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadMonth exposes calendar day statuses on success`() = runTest {
        val repository = FakeCalendarMonthlyRouteRepository()
        val viewModel = createViewModel(repository)

        viewModel.loadMonth(YearMonth.of(2026, 1))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val status = state.dayStatuses.getValue(LocalDate.of(2026, 1, 20))
        assertEquals(YearMonth.of(2026, 1), state.loadedMonth)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertTrue(status.hasLocationData)
        assertTrue(status.hasManualData)
        assertTrue(status.isBookmarked)
    }

    @Test
    fun `loadMonth skips duplicate request for already loaded month`() = runTest {
        val repository = FakeCalendarMonthlyRouteRepository()
        val viewModel = createViewModel(repository)

        viewModel.loadMonth(YearMonth.of(2026, 1))
        advanceUntilIdle()
        viewModel.loadMonth(YearMonth.of(2026, 1))
        advanceUntilIdle()

        assertEquals(listOf(YearMonth.of(2026, 1)), repository.requestedMonths)
    }

    @Test
    fun `loadMonth fetches again when visible month changes`() = runTest {
        val repository = FakeCalendarMonthlyRouteRepository()
        val viewModel = createViewModel(repository)

        viewModel.loadMonth(YearMonth.of(2026, 1))
        advanceUntilIdle()
        viewModel.loadMonth(YearMonth.of(2026, 2))
        advanceUntilIdle()

        assertEquals(
            listOf(YearMonth.of(2026, 1), YearMonth.of(2026, 2)),
            repository.requestedMonths
        )
        assertEquals(YearMonth.of(2026, 2), viewModel.uiState.value.loadedMonth)
    }

    @Test
    fun `loadMonth exposes error and retry clears it on success`() = runTest {
        val repository = FakeCalendarMonthlyRouteRepository(
            failure = RuntimeException("boom")
        )
        val viewModel = createViewModel(repository)

        viewModel.loadMonth(YearMonth.of(2026, 1))
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)

        repository.failure = null
        viewModel.loadMonth(
            visibleMonth = YearMonth.of(2026, 1),
            forceRefresh = true
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.errorMessage)
        assertEquals(YearMonth.of(2026, 1), state.loadedMonth)
    }

    private fun createViewModel(
        repository: CalendarMonthlyRouteRepository
    ): CalendarViewModel {
        return CalendarViewModel(
            getCalendarMonthlyRouteUseCase = GetCalendarMonthlyRouteUseCase(repository)
        )
    }
}

private class FakeCalendarMonthlyRouteRepository(
    var failure: Throwable? = null
) : CalendarMonthlyRouteRepository {
    val requestedMonths = mutableListOf<YearMonth>()

    override suspend fun getMonthlyDayRoutes(year: Int, month: Int): CalendarMonthlyRoute {
        requestedMonths += YearMonth.of(year, month)
        failure?.let { throw it }
        return CalendarMonthlyRoute(
            year = year,
            month = month,
            days = listOf(
                CalendarMonthlyDay(
                    date = LocalDate.of(year, month, 20),
                    hasManualData = true,
                    hasLocationData = true,
                    isBookmarked = true
                )
            )
        )
    }
}
