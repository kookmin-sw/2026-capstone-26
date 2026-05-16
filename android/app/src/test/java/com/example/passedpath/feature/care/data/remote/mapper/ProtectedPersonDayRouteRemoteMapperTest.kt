package com.example.passedpath.feature.care.data.remote.mapper

import com.example.passedpath.feature.care.data.remote.dto.CareDayRouteDetailResponseDto
import com.example.passedpath.feature.care.data.remote.dto.CareGpsPointItemDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.OffsetDateTime

class ProtectedPersonDayRouteRemoteMapperTest {

    @Test
    fun `toProtectedPersonDayRouteDetail maps response fields and recordedAt epoch millis`() {
        val recordedAt = "2026-04-29T13:32:43.059+09:00"
        val response = CareDayRouteDetailResponseDto(
            date = "2026-04-29",
            totalDistance = 3.25,
            gpsPoints = listOf(
                CareGpsPointItemDto(
                    recordedAt = recordedAt,
                    latitude = 37.498095,
                    longitude = 127.02761
                )
            )
        )

        val result = response.toProtectedPersonDayRouteDetail(
            requestedDateKey = "2026-04-30"
        )

        assertEquals("2026-04-29", result.dateKey)
        assertEquals(3.25, result.totalDistanceKm, 0.0)
        assertEquals(1, result.pathPointCount)
        assertEquals(1, result.routePoints.size)
        assertEquals(recordedAt, result.routePoints.first().recordedAt)
        assertEquals(
            OffsetDateTime.parse(recordedAt).toInstant().toEpochMilli(),
            result.routePoints.first().recordedAtEpochMillis
        )
        assertEquals(37.498095, result.routePoints.first().latitude, 0.0)
        assertEquals(127.02761, result.routePoints.first().longitude, 0.0)
    }

    @Test
    fun `toProtectedPersonDayRouteDetail falls back and keeps point when recordedAt is invalid`() {
        val response = CareDayRouteDetailResponseDto(
            date = null,
            totalDistance = null,
            gpsPoints = listOf(
                CareGpsPointItemDto(
                    recordedAt = "invalid-time",
                    latitude = 37.5,
                    longitude = 127.5
                ),
                CareGpsPointItemDto(
                    recordedAt = "2026-04-29T13:32:43.059+09:00",
                    latitude = null,
                    longitude = 127.6
                ),
                CareGpsPointItemDto(
                    recordedAt = "2026-04-29T13:32:43.059+09:00",
                    latitude = 37.6,
                    longitude = null
                )
            )
        )

        val result = response.toProtectedPersonDayRouteDetail(
            requestedDateKey = "2026-01-01"
        )

        assertEquals("2026-01-01", result.dateKey)
        assertEquals(0.0, result.totalDistanceKm, 0.0)
        assertEquals(1, result.pathPointCount)
        assertEquals(1, result.routePoints.size)
        assertEquals("invalid-time", result.routePoints.first().recordedAt)
        assertNull(result.routePoints.first().recordedAtEpochMillis)
    }
}
