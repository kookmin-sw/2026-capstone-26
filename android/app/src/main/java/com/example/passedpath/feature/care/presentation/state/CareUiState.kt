package com.example.passedpath.feature.care.presentation.state

import androidx.compose.runtime.Immutable

@Immutable
data class CareUiState(
    val dependents: List<CareDependentUserUiState> = emptyList(),
    val selectedDependentUserId: Long? = null,
    val isLoading: Boolean = false,
    val hasLoaded: Boolean = false,
    val errorMessage: String? = null
) {
    val selectedDependent: CareDependentUserUiState?
        get() = selectedDependentUserId?.let { selectedId ->
            dependents.firstOrNull { dependent -> dependent.dependentUserId == selectedId }
        }

    val mapMarkerDependents: List<CareDependentUserUiState>
        get() = dependents.filter(CareDependentUserUiState::hasLatestLocation)
}

@Immutable
data class CareDependentUserUiState(
    val dependentUserId: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val latestLatitude: Double?,
    val latestLongitude: Double?,
    val latestRecordedAt: String?
) {
    val hasLatestLocation: Boolean
        get() = latestLatitude != null && latestLongitude != null
}
