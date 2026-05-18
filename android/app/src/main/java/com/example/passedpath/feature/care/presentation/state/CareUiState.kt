package com.example.passedpath.feature.care.presentation.state

import androidx.compose.runtime.Immutable

// Care 화면 전체 상태를 한 객체로 묶는다.
// ViewModel은 값을 직접 바꾸지 않고 copy()로 새 상태를 만들어 Compose에 전달한다.
@Immutable
data class CareUiState(
    val dependents: List<CareDependentUserUiState> = emptyList(),
    val mapMarkers: List<CareDependentMapMarkerUiState> = emptyList(),
    val selectedDependentUserId: Long? = null,
    val isLoading: Boolean = false,
    val hasLoaded: Boolean = false,
    val errorMessage: String? = null
) {
    // 선택된 id가 있으면 보호 대상 목록에서 같은 id의 항목을 찾아 반환한다.
    val selectedDependent: CareDependentUserUiState?
        get() = selectedDependentUserId?.let { selectedId ->
            dependents.firstOrNull { dependent -> dependent.dependentUserId == selectedId }
        }
}

// 보호 대상 한 명을 Care 화면의 목록/선택 UI에서 쓰는 상태 모델이다.
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

// GoogleMap에 마커로 그릴 수 있는 보호 대상만 따로 담는 지도 전용 UI 모델이다.
// 지도 SDK 타입은 넣지 않고, 화면 상태에 필요한 순수 값만 보관한다.
@Immutable
data class CareDependentMapMarkerUiState(
    val dependentUserId: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val latitude: Double,
    val longitude: Double,
    val latestRecordedAt: String?
)
