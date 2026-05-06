package com.example.passedpath.feature.calendar.presentation.model

import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalendarMonthGridTest {

    @Test
    fun `buildCalendarMonthCells creates sunday based grid for january 2026`() {
        val cells = buildCalendarMonthCells(YearMonth.of(2026, 1))

        assertEquals(35, cells.size)
        repeat(4) { index ->
            assertNull(cells[index].date)
        }
        assertEquals(LocalDate.of(2026, 1, 1), cells[4].date)
        assertEquals(LocalDate.of(2026, 1, 31), cells[34].date)
    }

    @Test
    fun `buildCalendarMonthCells maps day statuses to matching dates`() {
        val status = CalendarDayStatus(
            hasManualData = true,
            hasLocationData = true,
            isBookmarked = true
        )
        val targetDate = LocalDate.of(2026, 1, 13)

        val cells = buildCalendarMonthCells(
            visibleMonth = YearMonth.of(2026, 1),
            dayStatuses = mapOf(targetDate to status)
        )

        assertEquals(status, cells.first { it.date == targetDate }.status)
    }

    @Test
    fun `toCalendarDateKey formats selected date for calendar cta`() {
        val dateKey = LocalDate.of(2026, 1, 19).toCalendarDateKey()

        assertEquals("2026-01-19", dateKey)
    }
}
