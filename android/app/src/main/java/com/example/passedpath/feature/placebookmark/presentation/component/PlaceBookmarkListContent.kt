package com.example.passedpath.feature.placebookmark.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passedpath.R
import com.example.passedpath.feature.placebookmark.domain.model.PlaceBookmarkSummary
import com.example.passedpath.feature.placebookmark.presentation.state.PlaceBookmarkUiState
import com.example.passedpath.ui.component.button.BaseButton
import com.example.passedpath.ui.component.feedback.WifiFailurePanel
import com.example.passedpath.ui.component.loading.BaseSkeletonBlock
import com.example.passedpath.ui.component.loading.rememberBaseSkeletonBrush
import com.example.passedpath.ui.component.menu.MenuActionItem
import com.example.passedpath.ui.theme.Gray200
import com.example.passedpath.ui.theme.Gray400
import com.example.passedpath.ui.theme.Gray50
import com.example.passedpath.ui.theme.Gray900
import com.example.passedpath.ui.theme.Green50
import com.example.passedpath.ui.theme.Green100
import com.example.passedpath.ui.theme.Green500
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
internal fun PlaceBookmarkListContent(
    uiState: PlaceBookmarkUiState,
    openedMenuBookmarkId: Long?,
    onMenuOpenedChange: (Long?) -> Unit,
    onAddPlaceBookmarkClick: () -> Unit,
    onEditPlaceBookmarkClick: (PlaceBookmarkSummary) -> Unit,
    onDeletePlaceBookmarkClick: (PlaceBookmarkSummary) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val isListScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                listState.firstVisibleItemScrollOffset > 0
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { isScrollInProgress ->
                if (isScrollInProgress) {
                    onMenuOpenedChange(null)
                }
            }
    }

    when {
        uiState.isLoading && !uiState.hasLoaded -> {
            Column(modifier = modifier) {
                PlaceBookmarkListDivider(visible = false)
                PlaceBookmarkLoadingSkeletonList(
                    placeCount = uiState.placeCount,
                    onAddPlaceBookmarkClick = onAddPlaceBookmarkClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }

        uiState.errorMessage != null && !uiState.hasLoaded -> {
            PlaceBookmarkErrorContent(
                message = uiState.errorMessage,
                onRetryClick = onRetryClick,
                modifier = modifier
            )
        }

        else -> {
            Column(
                modifier = modifier
            ) {
                PlaceBookmarkListDivider(visible = isListScrolled)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    state = listState,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 40.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item(key = "header") {
                        PlaceBookmarkListHeader(
                            placeCount = uiState.placeCount,
                            onAddPlaceBookmarkClick = onAddPlaceBookmarkClick
                        )
                    }

                    if (uiState.bookmarkPlaces.isEmpty()) {
                        item(key = "empty") {
                            PlaceBookmarkEmptyContent(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 120.dp)
                            )
                        }
                    } else {
                        items(
                            items = uiState.bookmarkPlaces,
                            key = PlaceBookmarkSummary::bookmarkPlaceId
                        ) { placeBookmark ->
                            PlaceBookmarkListItem(
                                placeBookmark = placeBookmark,
                                isMenuVisible = openedMenuBookmarkId == placeBookmark.bookmarkPlaceId,
                                onMoreClick = {
                                    onMenuOpenedChange(
                                        if (openedMenuBookmarkId == placeBookmark.bookmarkPlaceId) {
                                            null
                                        } else {
                                            placeBookmark.bookmarkPlaceId
                                        }
                                    )
                                },
                                onDismissMenu = { onMenuOpenedChange(null) },
                                onEditClick = {
                                    onMenuOpenedChange(null)
                                    onEditPlaceBookmarkClick(placeBookmark)
                                },
                                onDeleteClick = {
                                    onMenuOpenedChange(null)
                                    onDeletePlaceBookmarkClick(placeBookmark)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceBookmarkLoadingSkeletonList(
    placeCount: Int,
    onAddPlaceBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val skeletonBrush = rememberBaseSkeletonBrush()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 40.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "bookmark_loading_header") {
            PlaceBookmarkListHeader(
                placeCount = placeCount,
                onAddPlaceBookmarkClick = onAddPlaceBookmarkClick
            )
        }

        repeat(3) { index ->
            item(key = "bookmark_loading_card_$index") {
                PlaceBookmarkCardSkeleton(shimmerBrush = skeletonBrush)
            }
        }
    }
}

@Composable
private fun PlaceBookmarkListHeader(
    placeCount: Int,
    onAddPlaceBookmarkClick: () -> Unit
) {
    BaseButton(
        text = stringResource(R.string.place_bookmark_add_button),
        onClick = onAddPlaceBookmarkClick,
        border = BorderStroke(width = 1.dp, color = Green100),
        containerColor = Green50,
        contentColor = Green500,
        leadingIconResId = R.drawable.ic_plus,
        textFontSize = 16.sp,
        textFontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(24.dp))
    PlaceBookmarkCountText(placeCount = placeCount)
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun PlaceBookmarkCardSkeleton(
    shimmerBrush: Brush,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Gray50,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(
                PaddingValues(
                    start = 20.dp,
                    top = 14.dp,
                    end = 12.dp,
                    bottom = 14.dp
                )
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseSkeletonBlock(
            brush = shimmerBrush,
            modifier = Modifier.size(48.dp),
            shape = CircleShape
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            BaseSkeletonBlock(
                brush = shimmerBrush,
                modifier = Modifier
                    .fillMaxWidth(0.58f)
                    .height(16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            BaseSkeletonBlock(
                brush = shimmerBrush,
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .height(13.dp)
            )
        }

        BaseSkeletonBlock(
            brush = shimmerBrush,
            modifier = Modifier.size(36.dp),
            shape = CircleShape
        )
    }
}

@Composable
private fun PlaceBookmarkListDivider(visible: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(if (visible) Gray200 else Color.Transparent)
    )
}

@Composable
private fun PlaceBookmarkCountText(
    placeCount: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = buildAnnotatedString {
            append(stringResource(R.string.place_bookmark_count_prefix))
            withStyle(
                SpanStyle(
                    color = Green500,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(stringResource(R.string.place_bookmark_count_suffix, placeCount))
            }
        },
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        fontSize = 14.sp,
        color = Gray400,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun PlaceBookmarkListItem(
    placeBookmark: PlaceBookmarkSummary,
    isMenuVisible: Boolean,
    onMoreClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val editText = stringResource(R.string.place_menu_edit)
    val deleteText = stringResource(R.string.place_menu_delete)

    PlaceBookmarkCard(
        placeBookmark = placeBookmark,
        onMoreClick = onMoreClick,
        isMenuVisible = isMenuVisible,
        onDismissMenu = onDismissMenu,
        menuItems = listOf(
            MenuActionItem(
                text = editText,
                iconResId = R.drawable.ic_check,
                onClick = onEditClick
            ),
            MenuActionItem(
                text = deleteText,
                iconResId = R.drawable.ic_trash,
                onClick = onDeleteClick
            )
        ),
        modifier = modifier
    )
}

@Composable
private fun PlaceBookmarkErrorContent(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        WifiFailurePanel(
            title = stringResource(R.string.place_bookmark_error_title),
            message = message,
            retryText = stringResource(R.string.route_retry),
            onRetryClick = onRetryClick
        )
    }
}

@Composable
private fun PlaceBookmarkEmptyContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_information),
            contentDescription = null,
            tint = Gray400,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.place_bookmark_empty_title),
            style = MaterialTheme.typography.bodyLarge,
            color = Gray900,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.place_bookmark_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            color = Gray400
        )
    }
}
