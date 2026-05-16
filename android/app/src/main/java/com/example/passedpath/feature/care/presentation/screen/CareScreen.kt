package com.example.passedpath.feature.care.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.passedpath.R
import com.example.passedpath.feature.care.presentation.component.CareDependentMapMarker
import com.example.passedpath.feature.care.presentation.component.CareDependentSelectorRow
import com.example.passedpath.feature.care.presentation.component.careDependentAvatarPalette
import com.example.passedpath.feature.care.presentation.state.CareDependentUserUiState
import com.example.passedpath.feature.care.presentation.state.CareUiState
import com.example.passedpath.ui.component.feedback.NetworkFailureBanner
import com.example.passedpath.ui.theme.Gray500
import com.example.passedpath.ui.theme.Gray700
import com.example.passedpath.ui.theme.White
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun CareScreen(
    uiState: CareUiState,
    onDependentSelected: (Long?) -> Unit,
    onRetryClick: () -> Unit,
    onInviteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapStyleOptions = remember(context) {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.main_map_style)
    }
    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false
        )
    }
    val markerDependents = uiState.mapMarkerDependents
    val initialCameraTarget = markerDependents.firstOrNull()?.toLatLng() ?: SeoulFallbackLatLng
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialCameraTarget, InitialCareMapZoom)
    }
    val coroutineScope = rememberCoroutineScope()
    var isMapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(isMapLoaded, uiState.selectedDependentUserId, markerDependents) {
        if (!isMapLoaded) return@LaunchedEffect
        val targetDependent = uiState.selectedDependent
            ?.takeIf(CareDependentUserUiState::hasLatestLocation)
            ?: markerDependents.firstOrNull()
            ?: return@LaunchedEffect
        val zoom = if (uiState.selectedDependentUserId == null) {
            InitialCareMapZoom
        } else {
            SelectedCareMapZoom
        }
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(targetDependent.toLatLng(), zoom)
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = false,
                mapStyleOptions = mapStyleOptions
            ),
            uiSettings = mapUiSettings,
            onMapLoaded = { isMapLoaded = true },
            onMapClick = { onDependentSelected(null) }
        ) {
            markerDependents.forEachIndexed { index, dependent ->
                MarkerComposable(
                    state = MarkerState(position = dependent.toLatLng()),
                    title = dependent.nickname,
                    anchor = Offset(0.5f, 0.92f),
                    zIndex = if (uiState.selectedDependentUserId == dependent.dependentUserId) {
                        SelectedDependentMarkerZIndex
                    } else {
                        DependentMarkerZIndex
                    },
                    onClick = {
                        onDependentSelected(dependent.dependentUserId)
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    dependent.toLatLng(),
                                    SelectedCareMapZoom
                                )
                            )
                        }
                        true
                    }
                ) {
                    CareDependentMapMarker(
                        nickname = dependent.nickname,
                        profileImageUrl = dependent.profileImageUrl,
                        palette = careDependentAvatarPalette(index),
                        selected = uiState.selectedDependentUserId == dependent.dependentUserId
                    )
                }
            }
        }

        CareTopSelectorSurface(
            uiState = uiState,
            onDependentSelected = onDependentSelected,
            onInviteClick = onInviteClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        CareMapStatusOverlay(
            uiState = uiState,
            onRetryClick = onRetryClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun CareTopSelectorSurface(
    uiState: CareUiState,
    onDependentSelected: (Long?) -> Unit,
    onInviteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 14.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            CareDependentSelectorRow(
                dependents = uiState.dependents,
                selectedDependentUserId = uiState.selectedDependentUserId,
                isLoading = uiState.isLoading && !uiState.hasLoaded,
                onSelectAllClick = { onDependentSelected(null) },
                onDependentClick = { dependentUserId ->
                    onDependentSelected(dependentUserId)
                },
                onInviteClick = onInviteClick
            )
        }
    }
}

@Composable
private fun CareMapStatusOverlay(
    uiState: CareUiState,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = CareStatusTopPadding, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            uiState.errorMessage != null -> {
                NetworkFailureBanner(
                    retryText = stringResource(R.string.route_retry),
                    onRetryClick = onRetryClick
                )
            }

            uiState.hasLoaded && uiState.dependents.isEmpty() && !uiState.isLoading -> {
                CareEmptyDependentsNotice()
            }
        }
    }
}

@Composable
private fun CareEmptyDependentsNotice() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = White,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.care_dependents_empty_title),
                style = MaterialTheme.typography.bodyLarge,
                color = Gray700
            )
            Text(
                text = stringResource(R.string.care_dependents_empty_body),
                style = MaterialTheme.typography.bodyMedium,
                color = Gray500
            )
        }
    }
}

private fun CareDependentUserUiState.toLatLng(): LatLng {
    return LatLng(
        requireNotNull(latestLatitude),
        requireNotNull(latestLongitude)
    )
}

private val SeoulFallbackLatLng = LatLng(37.5662952, 126.9779451)
private val CareStatusTopPadding = 128.dp
private const val InitialCareMapZoom = 13f
private const val SelectedCareMapZoom = 16f
private const val DependentMarkerZIndex = 0f
private const val SelectedDependentMarkerZIndex = 1f
