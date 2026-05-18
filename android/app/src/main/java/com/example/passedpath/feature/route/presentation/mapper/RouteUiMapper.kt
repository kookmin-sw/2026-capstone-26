package com.example.passedpath.feature.route.presentation.mapper

import com.example.passedpath.feature.locationtracking.domain.model.DailyPath
import com.example.passedpath.feature.locationtracking.domain.model.DayRouteDetail
import com.example.passedpath.feature.locationtracking.domain.model.DayRoutePlace
import com.example.passedpath.feature.locationtracking.domain.model.RoutePoint
import com.example.passedpath.feature.locationtracking.domain.model.TrackedLocation
import com.example.passedpath.feature.route.presentation.state.MainRouteModeUiState
import com.example.passedpath.feature.route.presentation.state.PlaceMarkerUiState
import com.example.passedpath.feature.route.presentation.state.SelectedDayRouteUiState
import com.example.passedpath.ui.state.CoordinateUiState
import kotlin.math.cos
import kotlin.math.hypot

internal fun createInitialRouteMode(dateKey: String, isToday: Boolean): MainRouteModeUiState {
    val route = SelectedDayRouteUiState(dateKey = dateKey)
    return if (isToday) {
        createTodayRouteMode(route = route)
    } else {
        createPastRouteMode(route = route)
    }
}

internal fun createLoadingRouteMode(dateKey: String, isToday: Boolean): MainRouteModeUiState {
    val route = SelectedDayRouteUiState(dateKey = dateKey)
    return if (isToday) {
        createTodayRouteMode(
            route = route,
            isRouteLoading = true
        )
    } else {
        createPastRouteMode(
            route = route,
            isRouteLoading = true
        )
    }
}

internal fun createTodayEmptyRouteMode(dateKey: String): MainRouteModeUiState.Today {
    return createTodayRouteMode(
        route = SelectedDayRouteUiState(dateKey = dateKey),
        isRouteEmpty = true,
        routeEmptyMessage = "오늘의 이동 경로가 기록되면 이곳에 표시됩니다."
    )
}

internal fun createPastEmptyRouteMode(dateKey: String): MainRouteModeUiState.Past {
    return createPastRouteMode(
        route = SelectedDayRouteUiState(dateKey = dateKey),
        isRouteEmpty = true,
        routeEmptyMessage = "선택한 날짜에는 지도에 표시할 경로 데이터가 없습니다."
    )
}

internal fun createPastErrorRouteMode(dateKey: String): MainRouteModeUiState.Past {
    return createPastRouteMode(
        route = SelectedDayRouteUiState(dateKey = dateKey),
        routeErrorMessage = "선택한 날짜의 경로를 불러오지 못했습니다."
    )
}

internal fun createTodayRouteMode(
    route: SelectedDayRouteUiState,
    isRouteLoading: Boolean = false,
    isRouteEmpty: Boolean = false,
    routeEmptyMessage: String? = null,
    routeErrorMessage: String? = null
): MainRouteModeUiState.Today {
    return MainRouteModeUiState.Today(
        route = route,
        isRouteLoading = isRouteLoading,
        isRouteEmpty = isRouteEmpty,
        routeEmptyMessage = routeEmptyMessage,
        routeErrorMessage = routeErrorMessage
    )
}

internal fun createPastRouteMode(
    route: SelectedDayRouteUiState,
    isRouteLoading: Boolean = false,
    isRouteEmpty: Boolean = false,
    routeEmptyMessage: String? = null,
    routeErrorMessage: String? = null
): MainRouteModeUiState.Past {
    return MainRouteModeUiState.Past(
        route = route,
        isRouteLoading = isRouteLoading,
        isRouteEmpty = isRouteEmpty,
        routeEmptyMessage = routeEmptyMessage,
        routeErrorMessage = routeErrorMessage
    )
}

internal fun DailyPath.toSelectedDayRouteUiState(): SelectedDayRouteUiState {
    return SelectedDayRouteUiState(
        dateKey = dateKey,
        title = "",
        memo = "",
        isBookmarked = false,
        mapPolylinePoints = points.toTrackedMapPolylinePoints(),
        totalDistanceKm = totalDistanceMeters / 1000.0,
        pathPointCount = pathPointCount,
        markerPlaces = emptyList()
    )
}

internal fun createTodaySelectedDayRouteUiState(
    dateKey: String,
    dailyPath: DailyPath?,
    remoteRouteDetail: DayRouteDetail?
): SelectedDayRouteUiState {
    val remoteRouteUiState = remoteRouteDetail?.toSelectedDayRouteUiState()

    return SelectedDayRouteUiState(
        dateKey = dateKey,
        title = remoteRouteUiState?.title.orEmpty(),
        memo = remoteRouteUiState?.memo.orEmpty(),
        isBookmarked = remoteRouteUiState?.isBookmarked ?: false,
        mapPolylinePoints = dailyPath?.points?.toTrackedMapPolylinePoints().orEmpty(),
        totalDistanceKm = (dailyPath?.totalDistanceMeters ?: 0.0) / 1000.0,
        pathPointCount = dailyPath?.pathPointCount ?: 0,
        markerPlaces = remoteRouteUiState?.markerPlaces.orEmpty()
    )
}

internal fun DayRouteDetail.toSelectedDayRouteUiState(): SelectedDayRouteUiState {
    return SelectedDayRouteUiState(
        dateKey = dateKey,
        title = title,
        memo = memo,
        isBookmarked = isBookmarked,
        mapPolylinePoints = polylinePoints.toRouteMapPolylinePoints(),
        totalDistanceKm = totalDistanceKm,
        pathPointCount = pathPointCount,
        markerPlaces = places.map(DayRoutePlace::toPlaceMarkerUiState)
    )
}

internal fun patchRouteNoteSnapshot(
    routeModeUiState: MainRouteModeUiState,
    title: String?,
    memo: String?,
    shouldUpdateTitle: Boolean,
    shouldUpdateMemo: Boolean
): MainRouteModeUiState {
    if (!shouldUpdateTitle && !shouldUpdateMemo) return routeModeUiState

    return routeModeUiState.updateRouteSnapshot { route ->
        route.copy(
            title = if (shouldUpdateTitle) title.orEmpty() else route.title,
            memo = if (shouldUpdateMemo) memo.orEmpty() else route.memo
        )
    }
}

internal fun patchRouteBookmarkSnapshot(
    routeModeUiState: MainRouteModeUiState,
    isBookmarked: Boolean
): MainRouteModeUiState {
    return routeModeUiState.updateRouteSnapshot { route ->
        route.copy(isBookmarked = isBookmarked)
    }
}

private fun MainRouteModeUiState.updateRouteSnapshot(
    transform: (SelectedDayRouteUiState) -> SelectedDayRouteUiState
): MainRouteModeUiState {
    return when (this) {
        is MainRouteModeUiState.Today -> copy(route = transform(route))
        is MainRouteModeUiState.Past -> copy(route = transform(route))
    }
}

private fun List<TrackedLocation>.toTrackedMapPolylinePoints(): List<CoordinateUiState> {
    val indexes = simplifyMapPolylineIndexes(
        size = size,
        latitudeAt = { index -> this[index].latitude },
        longitudeAt = { index -> this[index].longitude }
    )
    return indexes.map { index -> this[index].toCoordinateUiState() }
}

private fun List<RoutePoint>.toRouteMapPolylinePoints(): List<CoordinateUiState> {
    val indexes = simplifyMapPolylineIndexes(
        size = size,
        latitudeAt = { index -> this[index].latitude },
        longitudeAt = { index -> this[index].longitude }
    )
    return indexes.map { index -> this[index].toCoordinateUiState() }
}

private fun simplifyMapPolylineIndexes(
    size: Int,
    latitudeAt: (Int) -> Double,
    longitudeAt: (Int) -> Double
): List<Int> {
    if (size <= MaxMapPolylinePoints) return List(size) { it }
    if (size <= 2) return List(size) { it }

    var lowToleranceMeters = 0.0
    var highToleranceMeters = InitialPolylineSimplificationToleranceMeters
    var bestIndexes = emptyList<Int>()

    while (highToleranceMeters <= MaxPolylineSimplificationToleranceMeters) {
        val indexes = ramerDouglasPeuckerIndexes(
            size = size,
            latitudeAt = latitudeAt,
            longitudeAt = longitudeAt,
            toleranceMeters = highToleranceMeters
        )
        if (indexes.size <= MaxMapPolylinePoints) {
            bestIndexes = indexes
            break
        }
        lowToleranceMeters = highToleranceMeters
        highToleranceMeters *= 2
    }

    repeat(PolylineSimplificationSearchIterations) {
        if (bestIndexes.isEmpty()) return@repeat
        val midToleranceMeters = (lowToleranceMeters + highToleranceMeters) / 2
        val indexes = ramerDouglasPeuckerIndexes(
            size = size,
            latitudeAt = latitudeAt,
            longitudeAt = longitudeAt,
            toleranceMeters = midToleranceMeters
        )
        if (indexes.size <= MaxMapPolylinePoints) {
            bestIndexes = indexes
            highToleranceMeters = midToleranceMeters
        } else {
            lowToleranceMeters = midToleranceMeters
        }
    }

    return if (bestIndexes.isEmpty()) {
        evenlyCapIndexes(List(size) { it }, MaxMapPolylinePoints)
    } else {
        evenlyCapIndexes(bestIndexes, MaxMapPolylinePoints)
    }
}

private fun ramerDouglasPeuckerIndexes(
    size: Int,
    latitudeAt: (Int) -> Double,
    longitudeAt: (Int) -> Double,
    toleranceMeters: Double
): List<Int> {
    val keep = BooleanArray(size)
    keep[0] = true
    keep[size - 1] = true

    val ranges = ArrayDeque<Pair<Int, Int>>()
    ranges.add(0 to size - 1)

    while (ranges.isNotEmpty()) {
        val (startIndex, endIndex) = ranges.removeLast()
        if (endIndex <= startIndex + 1) continue

        var farthestIndex = -1
        var farthestDistanceMeters = 0.0
        for (index in startIndex + 1 until endIndex) {
            val distanceMeters = perpendicularDistanceMeters(
                latitude = latitudeAt(index),
                longitude = longitudeAt(index),
                startLatitude = latitudeAt(startIndex),
                startLongitude = longitudeAt(startIndex),
                endLatitude = latitudeAt(endIndex),
                endLongitude = longitudeAt(endIndex)
            )
            if (distanceMeters > farthestDistanceMeters) {
                farthestDistanceMeters = distanceMeters
                farthestIndex = index
            }
        }

        if (farthestDistanceMeters > toleranceMeters && farthestIndex != -1) {
            keep[farthestIndex] = true
            ranges.add(startIndex to farthestIndex)
            ranges.add(farthestIndex to endIndex)
        }
    }

    return buildList {
        keep.forEachIndexed { index, shouldKeep ->
            if (shouldKeep) add(index)
        }
    }
}

private fun perpendicularDistanceMeters(
    latitude: Double,
    longitude: Double,
    startLatitude: Double,
    startLongitude: Double,
    endLatitude: Double,
    endLongitude: Double
): Double {
    val meanLatitudeRadians = Math.toRadians((latitude + startLatitude + endLatitude) / 3.0)
    val pointX = Math.toRadians(longitude - startLongitude) * cos(meanLatitudeRadians) * EarthRadiusMeters
    val pointY = Math.toRadians(latitude - startLatitude) * EarthRadiusMeters
    val endX = Math.toRadians(endLongitude - startLongitude) * cos(meanLatitudeRadians) * EarthRadiusMeters
    val endY = Math.toRadians(endLatitude - startLatitude) * EarthRadiusMeters
    val segmentLengthSquared = endX * endX + endY * endY

    if (segmentLengthSquared == 0.0) return hypot(pointX, pointY)

    val projectionRatio = ((pointX * endX + pointY * endY) / segmentLengthSquared).coerceIn(0.0, 1.0)
    val projectedX = projectionRatio * endX
    val projectedY = projectionRatio * endY
    return hypot(pointX - projectedX, pointY - projectedY)
}

private fun evenlyCapIndexes(indexes: List<Int>, maxSize: Int): List<Int> {
    if (indexes.size <= maxSize) return indexes
    if (maxSize <= 2) return listOf(indexes.first(), indexes.last()).distinct()

    val lastResultIndex = maxSize - 1
    val lastSourceIndex = indexes.lastIndex
    return List(maxSize) { resultIndex ->
        indexes[(resultIndex * lastSourceIndex) / lastResultIndex]
    }.distinct()
}

private fun TrackedLocation.toCoordinateUiState(): CoordinateUiState {
    return CoordinateUiState(
        latitude = latitude,
        longitude = longitude,
        recordedAtEpochMillis = recordedAtEpochMillis
    )
}

private const val MaxMapPolylinePoints = 800
private const val InitialPolylineSimplificationToleranceMeters = 2.0
private const val MaxPolylineSimplificationToleranceMeters = 10_000.0
private const val PolylineSimplificationSearchIterations = 12
private const val EarthRadiusMeters = 6_371_000.0

private fun RoutePoint.toCoordinateUiState(): CoordinateUiState {
    return CoordinateUiState(
        latitude = latitude,
        longitude = longitude
    )
}

private fun DayRoutePlace.toPlaceMarkerUiState(): PlaceMarkerUiState {
    return PlaceMarkerUiState(
        placeId = placeId,
        placeName = placeName,
        roadAddress = roadAddress,
        latitude = latitude,
        longitude = longitude,
        orderIndex = orderIndex
    )
}
