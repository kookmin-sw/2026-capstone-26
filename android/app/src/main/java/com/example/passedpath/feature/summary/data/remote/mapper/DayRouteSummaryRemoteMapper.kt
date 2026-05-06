package com.example.passedpath.feature.summary.data.remote.mapper

import com.example.passedpath.feature.summary.data.remote.dto.DayRouteSummaryResponseDto
import com.example.passedpath.feature.summary.domain.model.DayRouteSummary

internal fun DayRouteSummaryResponseDto.toDayRouteSummary(dateKey: String): DayRouteSummary {
    val seconds = totalOutingSeconds ?: 0L
    return DayRouteSummary(
        dateKey = dateKey,
        outingTime = outingTime.normalizedOrNull(),
        enterHomeTime = enterHomeTime.normalizedOrNull(),
        totalOutingCount = totalOutingCount ?: 0,
        totalOutingSeconds = seconds,
        totalOutingDurationText = totalOutingDurationText.normalizedOrNull()
            ?: seconds.toDurationText()
    )
}

private fun String?.normalizedOrNull(): String? {
    return this?.trim()?.takeIf(String::isNotEmpty)
}

private fun Long.toDurationText(): String {
    val safeSeconds = coerceAtLeast(0L)
    val hours = safeSeconds / SecondsPerHour
    val minutes = (safeSeconds % SecondsPerHour) / SecondsPerMinute

    return when {
        hours > 0L && minutes > 0L -> "${hours}${KoreanHour} ${minutes}${KoreanMinute}"
        hours > 0L -> "${hours}${KoreanHour}"
        minutes > 0L -> "${minutes}${KoreanMinute}"
        else -> "0${KoreanMinute}"
    }
}

private const val SecondsPerHour = 3_600L
private const val SecondsPerMinute = 60L
private const val KoreanHour = "\uC2DC\uAC04"
private const val KoreanMinute = "\uBD84"
