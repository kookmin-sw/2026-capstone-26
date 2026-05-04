package com.example.passedpath.feature.summary.presentation.state

data class DaySummaryUiState(
    val dateKey: String = "",
    val summary: DaySummaryContentUiState? = null,
    val hasLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class DaySummaryContentUiState(
    val outingTimeText: String,
    val enterHomeTimeText: String,
    val totalOutingDurationText: String,
    val totalOutingCountText: String
)
