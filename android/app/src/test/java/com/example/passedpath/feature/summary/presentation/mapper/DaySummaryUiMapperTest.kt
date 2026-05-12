package com.example.passedpath.feature.summary.presentation.mapper

import com.example.passedpath.feature.summary.domain.model.DayRouteSummary
import org.junit.Assert.assertEquals
import org.junit.Test

class DaySummaryUiMapperTest {

    @Test
    fun `toDaySummaryContentUiState formats offset time without timezone conversion`() {
        val summary = dayRouteSummary(
            outingTime = "2026-04-29T08:10:00+09:00",
            enterHomeTime = "2026-04-29T21:03:00+09:00",
            totalOutingCount = 3,
            totalOutingDurationText = "11\uC2DC\uAC04 51\uBD84",
            visitedDongNames = listOf("\uC815\uB989\uB3D9", "\uC131\uBD81\uB3D9")
        )

        val result = summary.toDaySummaryContentUiState()

        assertEquals("08:10", result.outingTimeText)
        assertEquals("21:03", result.enterHomeTimeText)
        assertEquals("11\uC2DC\uAC04 51\uBD84", result.totalOutingDurationText)
        assertEquals("3\uD68C", result.totalOutingCountText)
        assertEquals(listOf("\uC815\uB989\uB3D9", "\uC131\uBD81\uB3D9"), result.visitedDongNames)
    }

    @Test
    fun `toDaySummaryContentUiState displays dash for null or invalid times`() {
        val summary = dayRouteSummary(
            outingTime = null,
            enterHomeTime = "invalid"
        )

        val result = summary.toDaySummaryContentUiState()

        assertEquals("-", result.outingTimeText)
        assertEquals("-", result.enterHomeTimeText)
    }

    private fun dayRouteSummary(
        outingTime: String? = null,
        enterHomeTime: String? = null,
        totalOutingCount: Int = 0,
        totalOutingDurationText: String = "0\uBD84",
        visitedDongNames: List<String> = emptyList()
    ): DayRouteSummary {
        return DayRouteSummary(
            dateKey = "2026-04-29",
            outingTime = outingTime,
            enterHomeTime = enterHomeTime,
            totalOutingCount = totalOutingCount,
            totalOutingSeconds = 0L,
            totalOutingDurationText = totalOutingDurationText,
            visitedDongNames = visitedDongNames
        )
    }
}
