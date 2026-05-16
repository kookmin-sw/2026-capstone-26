package com.example.passedpath.feature.summary.presentation.state

data class DaySummaryUiState(
    val dateKey: String = "",
    val summary: DaySummaryContentUiState = DaySummaryContentUiState.Empty,
    val hasLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class DaySummaryContentUiState(
    val outingTimeText: String,
    val enterHomeTimeText: String,
    val totalOutingDurationText: String,
    val totalOutingCountText: String,
    val visitedDongNames: List<String> = emptyList()
) {
    companion object {
        val Empty = DaySummaryContentUiState(
            outingTimeText = "-",
            enterHomeTimeText = "-",
            totalOutingDurationText = "0\uBD84",
            totalOutingCountText = "0\uD68C",
            visitedDongNames = emptyList()
        )
    }
}
