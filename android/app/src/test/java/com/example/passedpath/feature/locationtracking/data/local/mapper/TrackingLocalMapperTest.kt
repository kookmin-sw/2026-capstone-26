package com.example.passedpath.feature.locationtracking.data.local.mapper

import com.example.passedpath.feature.locationtracking.data.local.entity.DayRouteEntity
import com.example.passedpath.feature.locationtracking.data.local.model.GpsPointRouteProjection
import org.junit.Assert.assertEquals
import org.junit.Test

class TrackingLocalMapperTest {

    @Test
    fun `route snapshot uses existing day route summary before projected point count`() {
        val points = listOf(
            GpsPointRouteProjection(
                recordedAtEpochMillis = 1L,
                latitude = 37.1,
                longitude = 127.1,
                accuracyMeters = 5f
            ),
            GpsPointRouteProjection(
                recordedAtEpochMillis = 2L,
                latitude = 37.2,
                longitude = 127.2,
                accuracyMeters = 8f
            )
        )
        val route = DayRouteEntity(
            dateKey = "2026-04-01",
            totalDistanceMeters = 1234.0,
            pathPointCount = 99,
            lastRecordedAtEpochMillis = 2L,
            lastSyncedAtEpochMillis = null
        )

        val snapshot = points.toLocalDayRouteSnapshot(
            dateKey = "2026-04-01",
            existingRoute = route
        )

        assertEquals("2026-04-01", snapshot.dateKey)
        assertEquals(2, snapshot.points.size)
        assertEquals(1234.0, snapshot.totalDistanceMeters, 0.0)
        assertEquals(99, snapshot.pathPointCount)
        assertEquals(1L, snapshot.points.first().recordedAtEpochMillis)
        assertEquals(2L, snapshot.points.last().recordedAtEpochMillis)
    }

    @Test
    fun `route snapshot falls back to projected point summary when route row is missing`() {
        val points = listOf(
            GpsPointRouteProjection(
                recordedAtEpochMillis = 10L,
                latitude = 37.1,
                longitude = 127.1,
                accuracyMeters = null
            )
        )

        val snapshot = points.toLocalDayRouteSnapshot(
            dateKey = "2026-04-01",
            existingRoute = null
        )

        assertEquals(1, snapshot.pathPointCount)
        assertEquals(0.0, snapshot.totalDistanceMeters, 0.0)
        assertEquals(10L, snapshot.points.single().recordedAtEpochMillis)
    }
}
