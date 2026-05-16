package com.example.passedpath.feature.care.presentation.mapper

import com.example.passedpath.feature.care.domain.model.CareDependentUser
import com.example.passedpath.feature.care.presentation.state.CareDependentUserUiState

fun CareDependentUser.toCareDependentUserUiState(): CareDependentUserUiState {
    return CareDependentUserUiState(
        dependentUserId = dependentUserId,
        nickname = nickname.ifBlank { "?" },
        profileImageUrl = profileImageUrl?.trim()?.takeIf(String::isNotEmpty),
        latestLatitude = latestGpsPoint?.latitude,
        latestLongitude = latestGpsPoint?.longitude,
        latestRecordedAt = latestGpsPoint?.recordedAt
    )
}
