package com.example.passedpath.feature.friends.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passedpath.R
import com.example.passedpath.feature.friends.presentation.model.ProtectedPersonBottomSheetTab
import com.example.passedpath.feature.friends.presentation.state.ProtectedPersonPlaceListUiState
import com.example.passedpath.feature.friends.presentation.state.ProtectedPersonPlaceUiState
import com.example.passedpath.feature.friends.presentation.state.ProtectedPersonSummaryContentUiState
import com.example.passedpath.feature.friends.presentation.state.ProtectedPersonSummaryUiState
import com.example.passedpath.feature.place.presentation.component.PlaceCard
import com.example.passedpath.feature.summary.presentation.component.DaySummaryMetricCard
import com.example.passedpath.feature.summary.presentation.component.DaySummaryMetricCardSkeleton
import com.example.passedpath.feature.summary.presentation.component.DaySummaryVisitedDongCard
import com.example.passedpath.feature.summary.presentation.component.DaySummaryVisitedDongCardSkeleton
import com.example.passedpath.ui.component.bottomsheet.BaseBottomSheetContainer
import com.example.passedpath.ui.component.bottomsheet.BaseBottomSheetTabItem
import com.example.passedpath.ui.component.bottomsheet.BaseBottomSheetTabRow
import com.example.passedpath.ui.component.feedback.NetworkFailureBanner
import com.example.passedpath.ui.component.loading.BaseSkeletonBlock
import com.example.passedpath.ui.component.loading.rememberBaseSkeletonBrush
import com.example.passedpath.ui.theme.Gray400
import com.example.passedpath.ui.theme.Gray50
import com.example.passedpath.ui.theme.Gray500
import com.example.passedpath.ui.theme.Gray700
import com.example.passedpath.ui.theme.Green100
import com.example.passedpath.ui.theme.Green300
import com.example.passedpath.ui.theme.Green50
import com.example.passedpath.ui.theme.Green500
import com.example.passedpath.ui.theme.PassedPathTheme

@Composable
fun ProtectedPersonBottomSheet(
    selectedTab: ProtectedPersonBottomSheetTab,
    onTabSelected: (ProtectedPersonBottomSheetTab) -> Unit,
    placeListUiState: ProtectedPersonPlaceListUiState,
    summaryUiState: ProtectedPersonSummaryUiState,
    modifier: Modifier = Modifier,
    onPlaceRetryClick: () -> Unit = {},
    onSummaryRetryClick: () -> Unit = {}
) {
    var isContentScrolled by remember(selectedTab) { mutableStateOf(false) }
    val tabItems = remember {
        ProtectedPersonBottomSheetTab.entries.map(ProtectedPersonBottomSheetTab::toBaseTabItem)
    }

    LaunchedEffect(selectedTab) {
        isContentScrolled = false
    }

    BaseBottomSheetContainer(
        modifier = modifier,
        isContentScrolled = isContentScrolled,
        tabRow = {
            BaseBottomSheetTabRow(
                items = tabItems,
                selectedKey = selectedTab,
                onTabSelected = onTabSelected
            )
        }
    ) {
        when (selectedTab) {
            ProtectedPersonBottomSheetTab.PLACE -> ProtectedPersonPlaceListContent(
                uiState = placeListUiState,
                onRetryClick = onPlaceRetryClick,
                onScrollStateChanged = { isContentScrolled = it },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            )

            ProtectedPersonBottomSheetTab.SUMMARY -> ProtectedPersonDaySummaryContent(
                uiState = summaryUiState,
                onRetryClick = onSummaryRetryClick,
                onScrollStateChanged = { isContentScrolled = it },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
fun ProtectedPersonPlaceListContent(
    uiState: ProtectedPersonPlaceListUiState,
    onRetryClick: () -> Unit,
    onScrollStateChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val isContentScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                listState.firstVisibleItemScrollOffset > 0
        }
    }

    LaunchedEffect(isContentScrolled) {
        onScrollStateChanged(isContentScrolled)
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(2.dp))
        }
        item {
            ProtectedPersonPlaceSummarySection(placeCount = uiState.placeCount)
        }
        item {
            ProtectedPersonPlaceInfoBanner()
        }

        when {
            uiState.errorMessage != null -> {
                item {
                    ProtectedPersonPlaceErrorNotice(onRetryClick = onRetryClick)
                }
            }

            uiState.isLoading || !uiState.hasLoaded -> {
                item(key = "protected_person_place_skeleton") {
                    ProtectedPersonPlaceSkeletonList()
                }
            }

            uiState.places.isEmpty() -> {
                item {
                    ProtectedPersonEmptyPlaceNotice()
                }
            }

            else -> {
                items(
                    items = uiState.places,
                    key = ProtectedPersonPlaceUiState::placeId
                ) { place ->
                    PlaceCard(
                        name = place.placeName,
                        address = place.roadAddress,
                        startTimeText = place.startTimeText,
                        endTimeText = place.endTimeText,
                        isFavoritePlace = place.isFavoritePlace,
                        showMoreButton = false,
                        onClick = null
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ProtectedPersonDaySummaryContent(
    uiState: ProtectedPersonSummaryUiState,
    onRetryClick: () -> Unit,
    onScrollStateChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val isContentScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                listState.firstVisibleItemScrollOffset > 0
        }
    }

    LaunchedEffect(isContentScrolled) {
        onScrollStateChanged(isContentScrolled)
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(2.dp))
        }

        when {
            uiState.errorMessage != null -> {
                item {
                    NetworkFailureBanner(
                        retryText = stringResource(R.string.route_retry),
                        onRetryClick = onRetryClick
                    )
                }
            }

            uiState.isLoading || !uiState.hasLoaded -> {
                item(key = "protected_person_summary_skeleton") {
                    ProtectedPersonSummarySkeletonList()
                }
            }

            else -> {
                item {
                    DaySummaryMetricCard(
                        label = stringResource(R.string.day_summary_outing_time),
                        value = uiState.summary.outingTimeText
                    )
                }
                item {
                    DaySummaryMetricCard(
                        label = stringResource(R.string.day_summary_enter_home_time),
                        value = uiState.summary.enterHomeTimeText
                    )
                }
                item {
                    DaySummaryMetricCard(
                        label = stringResource(R.string.day_summary_total_outing_duration),
                        value = uiState.summary.totalOutingDurationText
                    )
                }
                item {
                    DaySummaryMetricCard(
                        label = stringResource(R.string.day_summary_total_outing_count),
                        value = uiState.summary.totalOutingCountText
                    )
                }
                item {
                    DaySummaryVisitedDongCard(
                        label = stringResource(R.string.day_summary_visited_dong_title),
                        visitedDongNames = uiState.summary.visitedDongNames
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ProtectedPersonPlaceSummarySection(placeCount: Int) {
    Text(
        text = buildAnnotatedString {
            append(stringResource(R.string.place_sheet_visit_count_prefix))
            withStyle(SpanStyle(color = Green300)) {
                append(stringResource(R.string.place_sheet_visit_count_suffix, placeCount))
            }
        },
        style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        ),
        color = Gray500
    )
}

@Composable
private fun ProtectedPersonPlaceInfoBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Green50, RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, Green100), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_info_circle),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = stringResource(R.string.protected_person_place_banner),
            style = MaterialTheme.typography.bodySmall,
            color = Green500,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ProtectedPersonPlaceErrorNotice(onRetryClick: () -> Unit) {
    NetworkFailureBanner(
        retryText = stringResource(R.string.route_retry),
        onRetryClick = onRetryClick
    )
}

@Composable
private fun ProtectedPersonEmptyPlaceNotice() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray50, RoundedCornerShape(20.dp))
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.protected_person_place_empty_title),
            style = MaterialTheme.typography.bodyLarge,
            color = Gray700,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.protected_person_place_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            color = Gray400,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProtectedPersonPlaceSkeletonList() {
    val skeletonBrush = rememberBaseSkeletonBrush()

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        repeat(3) {
            ProtectedPersonPlaceCardSkeleton(shimmerBrush = skeletonBrush)
        }
    }
}

@Composable
private fun ProtectedPersonPlaceCardSkeleton(
    shimmerBrush: Brush,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp)
            .background(Gray50, RoundedCornerShape(24.dp))
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            BaseSkeletonBlock(
                brush = shimmerBrush,
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .height(18.dp),
                shape = RoundedCornerShape(9.dp)
            )
            BaseSkeletonBlock(
                brush = shimmerBrush,
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .height(14.dp)
            )
            BaseSkeletonBlock(
                brush = shimmerBrush,
                modifier = Modifier
                    .fillMaxWidth(0.44f)
                    .height(14.dp)
            )
        }
        BaseSkeletonBlock(
            brush = shimmerBrush,
            modifier = Modifier.size(34.dp),
            shape = CircleShape
        )
    }
}

@Composable
private fun ProtectedPersonSummarySkeletonList() {
    val skeletonBrush = rememberBaseSkeletonBrush()

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        repeat(4) {
            DaySummaryMetricCardSkeleton(shimmerBrush = skeletonBrush)
        }
        DaySummaryVisitedDongCardSkeleton(shimmerBrush = skeletonBrush)
    }
}

private fun ProtectedPersonBottomSheetTab.titleResId(): Int {
    return when (this) {
        ProtectedPersonBottomSheetTab.PLACE -> R.string.record_sheet_tab_place
        ProtectedPersonBottomSheetTab.SUMMARY -> R.string.record_sheet_tab_summary
    }
}

private fun ProtectedPersonBottomSheetTab.iconResId(): Int {
    return when (this) {
        ProtectedPersonBottomSheetTab.PLACE -> R.drawable.ic_bottom_sheet_place
        ProtectedPersonBottomSheetTab.SUMMARY -> R.drawable.ic_summary_day
    }
}

private fun ProtectedPersonBottomSheetTab.toBaseTabItem():
    BaseBottomSheetTabItem<ProtectedPersonBottomSheetTab> {
    return BaseBottomSheetTabItem(
        key = this,
        titleResId = titleResId(),
        iconResId = iconResId()
    )
}

@Preview(showBackground = true, heightDp = 720, name = "Protected Person - Places")
@Composable
private fun ProtectedPersonBottomSheetPlacePreview() {
    ProtectedPersonBottomSheetPreviewScaffold(
        selectedTab = ProtectedPersonBottomSheetTab.PLACE,
        placeListUiState = SampleProtectedPersonPlaceListUiState
    )
}

@Preview(showBackground = true, heightDp = 720, name = "Protected Person - Summary")
@Composable
private fun ProtectedPersonBottomSheetSummaryPreview() {
    ProtectedPersonBottomSheetPreviewScaffold(
        selectedTab = ProtectedPersonBottomSheetTab.SUMMARY,
        summaryUiState = SampleProtectedPersonSummaryUiState
    )
}

@Preview(showBackground = true, heightDp = 720, name = "Protected Person - Loading")
@Composable
private fun ProtectedPersonBottomSheetLoadingPreview() {
    ProtectedPersonBottomSheetPreviewScaffold(
        selectedTab = ProtectedPersonBottomSheetTab.PLACE,
        placeListUiState = ProtectedPersonPlaceListUiState(
            placeCount = 0,
            isLoading = true
        )
    )
}

@Preview(showBackground = true, heightDp = 720, name = "Protected Person - Empty")
@Composable
private fun ProtectedPersonBottomSheetEmptyPreview() {
    ProtectedPersonBottomSheetPreviewScaffold(
        selectedTab = ProtectedPersonBottomSheetTab.PLACE,
        placeListUiState = ProtectedPersonPlaceListUiState(
            placeCount = 0,
            hasLoaded = true
        )
    )
}

@Composable
private fun ProtectedPersonBottomSheetPreviewScaffold(
    selectedTab: ProtectedPersonBottomSheetTab,
    placeListUiState: ProtectedPersonPlaceListUiState = SampleProtectedPersonPlaceListUiState,
    summaryUiState: ProtectedPersonSummaryUiState = SampleProtectedPersonSummaryUiState
) {
    PassedPathTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F4F6))
        ) {
            ProtectedPersonBottomSheet(
                selectedTab = selectedTab,
                onTabSelected = {},
                placeListUiState = placeListUiState,
                summaryUiState = summaryUiState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private val SampleProtectedPersonPlaceListUiState = ProtectedPersonPlaceListUiState(
    places = listOf(
        ProtectedPersonPlaceUiState(
            placeId = 1L,
            placeName = "Maison Akai Hyehwa",
            roadAddress = "34 Daemyeong-gil, Jongno-gu, Seoul",
            startTimeText = "10:12 AM",
            endTimeText = "11:08 AM"
        ),
        ProtectedPersonPlaceUiState(
            placeId = 2L,
            placeName = "National University Welfare Center",
            roadAddress = "77 Jeongneung-ro, Seongbuk-gu, Seoul",
            startTimeText = "12:30 PM",
            endTimeText = "1:15 PM",
            isFavoritePlace = true
        ),
        ProtectedPersonPlaceUiState(
            placeId = 3L,
            placeName = "Cheonggye Plaza",
            roadAddress = "Taepyeong-ro 1-ga, Jung-gu, Seoul",
            startTimeText = "4:20 PM",
            endTimeText = "5:05 PM"
        )
    ),
    placeCount = 3,
    hasLoaded = true
)

private val SampleProtectedPersonSummaryUiState = ProtectedPersonSummaryUiState(
    hasLoaded = true,
    summary = ProtectedPersonSummaryContentUiState(
        outingTimeText = "09:12",
        enterHomeTimeText = "21:03",
        totalOutingDurationText = "11h 51m",
        totalOutingCountText = "3",
        visitedDongNames = listOf("Hyehwa-dong", "Jeongneung-dong", "Seongbuk-dong")
    )
)
