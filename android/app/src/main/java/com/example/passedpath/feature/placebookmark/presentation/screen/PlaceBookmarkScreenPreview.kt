package com.example.passedpath.feature.placebookmark.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.passedpath.feature.place.domain.model.BookmarkPlaceType
import com.example.passedpath.feature.placebookmark.domain.model.PlaceBookmarkSummary
import com.example.passedpath.feature.placebookmark.presentation.state.PlaceBookmarkUiState
import com.example.passedpath.ui.theme.PassedPathTheme

@Preview(showBackground = true)
@Composable
private fun PlaceBookmarkScreenPreview() {
    PassedPathTheme {
        PlaceBookmarkScreen(
            uiState = PlaceBookmarkUiState(
                placeCount = 3,
                hasLoaded = true,
                bookmarkPlaces = listOf(
                    previewPlaceBookmark(1L, BookmarkPlaceType.SCHOOL),
                    previewPlaceBookmark(2L, BookmarkPlaceType.COMPANY),
                    previewPlaceBookmark(3L, BookmarkPlaceType.HOME)
                )
            ),
            searchResultEvent = null,
            onSearchResultEventConsumed = {},
            onBackClick = {},
            onAddPlaceBookmarkClick = {},
            onRetryClick = {},
            onCreatePlaceBookmark = { _, _, _, _, _ -> },
            onUpdatePlaceBookmark = { _, _, _, _, _, _ -> },
            onDeletePlaceBookmark = {},
            onFeedbackDismissed = {}
        )
    }
}

private fun previewPlaceBookmark(
    id: Long,
    type: BookmarkPlaceType
): PlaceBookmarkSummary {
    return PlaceBookmarkSummary(
        bookmarkPlaceId = id,
        type = type,
        placeName = "국민대학교 복지관",
        roadAddress = "서울 성북구 정릉로 77",
        latitude = 37.6113,
        longitude = 126.9958
    )
}
