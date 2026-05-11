package com.example.passedpath.feature.main.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.passedpath.BuildConfig
import com.example.passedpath.R
import com.example.passedpath.feature.main.presentation.component.MainMoreActionSheet
import com.example.passedpath.feature.main.presentation.state.MainUiState
import com.example.passedpath.feature.permission.presentation.mapper.createPermissionOverlayUiModel
import com.example.passedpath.feature.route.presentation.screen.RouteStatusOverlay
import com.example.passedpath.feature.route.presentation.screen.RouteTopCenterControls
import com.example.passedpath.feature.route.presentation.screen.RouteTopEndControls
import com.example.passedpath.feature.route.presentation.state.MainRouteModeUiState
import com.example.passedpath.feature.route.presentation.state.RouteUiAction
import com.example.passedpath.ui.component.modal.PassedPathBottomModal
import com.example.passedpath.ui.component.floating.FloatingButtonColumn
import com.example.passedpath.ui.component.banner.RequestActionBottomBanner
import com.example.passedpath.ui.component.loading.BaseLoadingLine
import com.example.passedpath.ui.theme.Black

private val RouteOverlayTopGap = 21.dp
private val RouteOverlaySidePadding = 16.dp
private val RouteFloatingButtonSize = 40.dp
private val RouteErrorBannerGapFromFloatingButton = 12.dp
private val RouteErrorBannerStartPadding =
    RouteOverlaySidePadding + RouteFloatingButtonSize + RouteErrorBannerGapFromFloatingButton

@Composable
internal fun BoxScope.MainMapOverlayContent(
    uiState: MainUiState,
    onDateSelected: (String) -> Unit,
    onBookmarkClick: () -> Unit,
    isMoreMenuVisible: Boolean,
    onMoreClick: () -> Unit,
    onMoreDismissRequest: () -> Unit,
    onMorePlaceBookmarkClick: () -> Unit,
    onMoreDeleteRecordClick: () -> Unit,
    onRouteAction: (RouteUiAction) -> Unit,
    onPermissionActionClick: () -> Unit,
    debugActions: MainDebugActions,
    floatingBottomPadding: Dp,
    bottomEndControlsBottomPadding: Dp = floatingBottomPadding,
    isDebugPanelVisible: Boolean,
    onCloseDebugPanel: () -> Unit,
    topStartControls: @Composable (() -> Unit)? = null,
    floatingControls: @Composable (() -> Unit)? = null
) {
    val permissionOverlayUiModel = createPermissionOverlayUiModel(
        permissionState = uiState.permissionState,
        isLocationServiceEnabled = uiState.isLocationServiceEnabled
    )
    val routeModeUiState = uiState.routeModeUiState
    val pastRouteErrorMessage = (routeModeUiState as? MainRouteModeUiState.Past)?.routeErrorMessage
    val isPastRouteLoading = routeModeUiState is MainRouteModeUiState.Past &&
        routeModeUiState.isRouteLoading

    if (pastRouteErrorMessage == null) {
        RouteStatusOverlay(
            routeModeUiState = routeModeUiState,
            onRouteAction = onRouteAction
        )
    }

    RouteTopBars(
        route = uiState.selectedRoute,
        isBookmarkUpdating = uiState.bookmarkToggleUiState.isUpdating(uiState.selectedDateKey),
        onDateSelected = onDateSelected,
        onBookmarkClick = onBookmarkClick,
        onMoreClick = onMoreClick,
        modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
    )

    when {
        isPastRouteLoading -> {
            BaseLoadingLine(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = RouteTopBarsHeight)
            )
        }

        pastRouteErrorMessage != null -> {
            RequestActionBottomBanner(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        top = RouteTopBarsHeight + RouteOverlayTopGap,
                        start = RouteErrorBannerStartPadding,
                        end = RouteOverlaySidePadding
                    ),
                message = pastRouteErrorMessage,
                actionText = stringResource(R.string.route_retry),
                onClickAction = { onRouteAction(RouteUiAction.RetryPastRoute) },
                borderColor = null,
                shadowElevation = 6.dp
            )
        }
    }

    FloatingButtonColumn(
        modifier = Modifier
            .align(Alignment.TopStart)
            .statusBarsPadding()
            .padding(
                top = RouteTopBarsHeight + RouteOverlayTopGap,
                start = RouteOverlaySidePadding
            )
    ) {
        topStartControls?.invoke()
    }

    FloatingButtonColumn(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .statusBarsPadding()
            .padding(
                top = RouteTopBarsHeight + RouteOverlayTopGap,
                end = RouteOverlaySidePadding
            )
    ) {
        RouteTopEndControls(
            routeMode = uiState.routeModeUiState,
            onRouteAction = onRouteAction
        )
    }

    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = RouteTopBarsHeight + 160.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (pastRouteErrorMessage == null) {
            RouteTopCenterControls(
                routeMode = routeModeUiState,
                onRouteAction = onRouteAction
            )
        }
        if (BuildConfig.DEBUG && isDebugPanelVisible) {
            MainDebugPanel(
                debugUiState = uiState.debugUiState,
                onRefreshSystemState = debugActions.refreshSystemState,
                onReloadRoute = debugActions.reloadRoute,
                onClose = onCloseDebugPanel
            )
        }
    }

    FloatingButtonColumn(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 16.dp, bottom = bottomEndControlsBottomPadding)
    ) {
        floatingControls?.invoke()
    }

    permissionOverlayUiModel?.let { overlayUiModel ->
        RequestActionBottomBanner(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(bottom = floatingBottomPadding),
            message = stringResource(overlayUiModel.messageResId),
            actionText = stringResource(overlayUiModel.actionTextResId),
            onClickAction = onPermissionActionClick
        )
    }

    if (isMoreMenuVisible) {
        PassedPathBottomModal(
            onDimClick = onMoreDismissRequest,
            modifier = Modifier.background(Black.copy(alpha = 0.22f)),
            onBackPress = onMoreDismissRequest
        ) {
            MainMoreActionSheet(
                onPlaceBookmarkClick = onMorePlaceBookmarkClick,
                onDeleteRecordClick = onMoreDeleteRecordClick
            )
        }
    }
}
