package com.example.passedpath.feature.care.domain.model

data class ProtectedPersonRoutePoint(
    val recordedAt: String?,
    val recordedAtEpochMillis: Long?,
    val latitude: Double,
    val longitude: Double
)

data class ProtectedPersonDayRouteDetail(
    val dateKey: String,
    val totalDistanceKm: Double,
    val pathPointCount: Int,
    val routePoints: List<ProtectedPersonRoutePoint> = emptyList()
)
