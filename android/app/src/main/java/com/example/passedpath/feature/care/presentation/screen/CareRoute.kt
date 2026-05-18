package com.example.passedpath.feature.care.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.passedpath.app.appContainer
import com.example.passedpath.feature.care.presentation.viewmodel.CareViewModel
import com.example.passedpath.feature.care.presentation.viewmodel.CareViewModelFactory

@Composable
fun CareRoute(
    modifier: Modifier = Modifier,
    viewModel: CareViewModel = viewModel(
        factory = CareViewModelFactory(LocalContext.current.appContainer)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CareScreen(
        uiState = uiState,
        onDependentSelected = viewModel::selectDependent,
        onRetryClick = viewModel::refreshDependents,
        onInviteClick = {},
        onSheetValueChanged = viewModel::onSheetValueChanged,
        onSheetCommandConsumed = viewModel::onSheetCommandConsumed,
        onTabSelected = viewModel::selectBottomSheetTab,
        onPlaceMarkerClick = viewModel::onPlaceMarkerClick,
        onPlaceCardClick = viewModel::onPlaceCardClick,
        onSelectedPlaceHandled = viewModel::onSelectedPlaceHandled,
        onFocusedPlaceHandled = viewModel::onFocusedPlaceHandled,
        onMapClick = viewModel::onMapClick,
        onPlaceRetryClick = viewModel::retryProtectedPersonPlaces,
        onSummaryRetryClick = viewModel::retryProtectedPersonSummary,
        modifier = modifier
    )
}
