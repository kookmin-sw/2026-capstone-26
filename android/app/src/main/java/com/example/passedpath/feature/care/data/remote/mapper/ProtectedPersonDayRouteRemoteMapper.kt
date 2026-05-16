package com.example.passedpath.feature.care.data.remote.mapper

import com.example.passedpath.feature.care.data.remote.dto.CareDayRouteDetailResponseDto
import com.example.passedpath.feature.care.data.remote.dto.CareGpsPointItemDto
import com.example.passedpath.feature.care.domain.model.ProtectedPersonDayRouteDetail
import com.example.passedpath.feature.care.domain.model.ProtectedPersonRoutePoint
import java.time.OffsetDateTime

internal fun CareDayRouteDetailResponseDto.toProtectedPersonDayRouteDetail(
    requestedDateKey: String
): ProtectedPersonDayRouteDetail {
    val routePoints = gpsPoints.orEmpty()
        .mapNotNull(CareGpsPointItemDto::toProtectedPersonRoutePointOrNull)

    return ProtectedPersonDayRouteDetail(
        dateKey = date ?: requestedDateKey,
        totalDistanceKm = totalDistance ?: 0.0,
        pathPointCount = routePoints.size,
        routePoints = routePoints
    )
}

private fun CareGpsPointItemDto.toProtectedPersonRoutePointOrNull(): ProtectedPersonRoutePoint? {
    val lat = latitude ?: return null
    val lng = longitude ?: return null

    return ProtectedPersonRoutePoint(
        recordedAt = recordedAt,
        recordedAtEpochMillis = recordedAt.toEpochMillisOrNull(),
        latitude = lat,
        longitude = lng
    )
}

private fun String?.toEpochMillisOrNull(): Long? {
    if (isNullOrBlank()) return null

    return runCatching {
        OffsetDateTime.parse(this)
            .toInstant()
            .toEpochMilli()
    }.getOrNull()
}
