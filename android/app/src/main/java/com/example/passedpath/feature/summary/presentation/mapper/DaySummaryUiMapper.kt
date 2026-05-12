package com.example.passedpath.feature.summary.presentation.mapper

import com.example.passedpath.feature.summary.domain.model.DayRouteSummary
import com.example.passedpath.feature.summary.presentation.state.DaySummaryContentUiState
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

internal fun DayRouteSummary.toDaySummaryContentUiState(): DaySummaryContentUiState {
    return DaySummaryContentUiState(
        outingTimeText = outingTime.toSummaryTimeText(),
        enterHomeTimeText = enterHomeTime.toSummaryTimeText(),
        totalOutingDurationText = totalOutingDurationText,
        totalOutingCountText = "${totalOutingCount}${KoreanCount}",
        visitedDongNames = visitedDongNames
    )
}

internal fun String?.toSummaryTimeText(): String {
    val timestamp = this?.trim()?.takeIf(String::isNotEmpty) ?: return EmptySummaryValue
    return runCatching {
        OffsetDateTime.parse(timestamp).format(SummaryTimeFormatter)
    }.getOrDefault(EmptySummaryValue)
}

private val SummaryTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm", Locale.KOREA)

private const val EmptySummaryValue = "-"
private const val KoreanCount = "\uD68C"
