package com.example.passedpath.feature.summary.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.passedpath.R
import com.example.passedpath.app.appContainer
import com.example.passedpath.feature.summary.presentation.component.WeeklySummaryMetricCard
import com.example.passedpath.feature.summary.presentation.component.WeeklySummaryMetricCardSkeleton
import com.example.passedpath.feature.summary.presentation.component.WeeklySummaryVisitedRegionsCard
import com.example.passedpath.feature.summary.presentation.component.WeeklySummaryVisitedRegionsCardSkeleton
import com.example.passedpath.feature.summary.presentation.state.WeeklySummaryBarUiState
import com.example.passedpath.feature.summary.presentation.state.WeeklySummaryContentUiState
import com.example.passedpath.feature.summary.presentation.state.WeeklySummaryMetricCardUiState
import com.example.passedpath.feature.summary.presentation.state.WeeklySummaryUiState
import com.example.passedpath.feature.summary.presentation.state.WeeklySummaryVisitedRegionUiState
import com.example.passedpath.feature.summary.presentation.state.WeeklySummaryVisitedRegionsCardUiState
import com.example.passedpath.feature.summary.presentation.viewmodel.WeeklySummaryViewModel
import com.example.passedpath.feature.summary.presentation.viewmodel.WeeklySummaryViewModelFactory
import com.example.passedpath.ui.component.feedback.NetworkFailureBanner
import com.example.passedpath.ui.component.loading.rememberBaseSkeletonBrush
import com.example.passedpath.ui.theme.Gray900
import com.example.passedpath.ui.theme.PassedPathTheme
import com.example.passedpath.ui.theme.White

@Composable
fun WeeklySummaryRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeeklySummaryViewModel = viewModel(
        factory = WeeklySummaryViewModelFactory(LocalContext.current.appContainer)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.loadWeeklySummary()
    }

    WeeklySummaryScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetryClick = { viewModel.loadWeeklySummary(forceRefresh = true) },
        modifier = modifier
    )
}

@Composable
internal fun WeeklySummaryScreen(
    uiState: WeeklySummaryUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding()
    ) {
        WeeklySummaryTopBar(onBackClick = onBackClick)

        when {
            !uiState.hasLoaded && uiState.errorMessage == null -> {
                WeeklySummarySkeletonList(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            uiState.errorMessage != null && !uiState.hasLoaded -> {
                WeeklySummaryInitialError(
                    onRetryClick = onRetryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            else -> {
                WeeklySummaryLoadedList(
                    uiState = uiState,
                    onRetryClick = onRetryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
private fun WeeklySummaryTopBar(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = stringResource(R.string.calendar_back),
                tint = Gray900,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = stringResource(R.string.weekly_summary_title),
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleMedium,
            color = Gray900,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun WeeklySummaryLoadedList(
    uiState: WeeklySummaryUiState,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 12.dp,
            end = 20.dp,
            bottom = 40.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (uiState.errorMessage != null) {
            item(key = "weekly_summary_error") {
                NetworkFailureBanner(
                    retryText = stringResource(R.string.route_retry),
                    onRetryClick = onRetryClick
                )
            }
        }

        uiState.summary.metricCards.forEachIndexed { index, card ->
            item(key = "weekly_summary_metric_$index") {
                WeeklySummaryMetricCard(card = card)
            }
        }

        item(key = "weekly_summary_visited_regions") {
            WeeklySummaryVisitedRegionsCard(card = uiState.summary.visitedRegionsCard)
        }
    }
}

@Composable
private fun WeeklySummarySkeletonList(
    modifier: Modifier = Modifier
) {
    val skeletonBrush = rememberBaseSkeletonBrush()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 12.dp,
            end = 20.dp,
            bottom = 40.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        repeat(4) { index ->
            item(key = "weekly_summary_metric_skeleton_$index") {
                WeeklySummaryMetricCardSkeleton(shimmerBrush = skeletonBrush)
            }
        }
        item(key = "weekly_summary_visited_skeleton") {
            WeeklySummaryVisitedRegionsCardSkeleton(shimmerBrush = skeletonBrush)
        }
    }
}

@Composable
private fun WeeklySummaryInitialError(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        NetworkFailureBanner(
            retryText = stringResource(R.string.route_retry),
            onRetryClick = onRetryClick
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WeeklySummaryScreenPreview() {
    PassedPathTheme {
        WeeklySummaryScreen(
            uiState = WeeklySummaryUiState(
                hasLoaded = true,
                summary = previewWeeklySummaryContent()
            ),
            onBackClick = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WeeklySummaryScreenLoadingPreview() {
    PassedPathTheme {
        WeeklySummaryScreen(
            uiState = WeeklySummaryUiState(isLoading = true),
            onBackClick = {},
            onRetryClick = {}
        )
    }
}

private fun previewWeeklySummaryContent(): WeeklySummaryContentUiState {
    return WeeklySummaryContentUiState(
        metricCards = listOf(
            WeeklySummaryMetricCardUiState("주간 외출 시간", valueText = "09:12", bars = previewBars()),
            WeeklySummaryMetricCardUiState("주간 귀가 시간", valueText = "23:15", bars = previewBars()),
            WeeklySummaryMetricCardUiState("주간 총 외출 시간", valueText = "5시간 30분", bars = previewBars()),
            WeeklySummaryMetricCardUiState("주간 외출 횟수", valueText = "1.3회", bars = previewBars())
        ),
        visitedRegionsCard = WeeklySummaryVisitedRegionsCardUiState(
            title = "자주 방문한 동네",
            regions = listOf(
                WeeklySummaryVisitedRegionUiState("1위", "성북구"),
                WeeklySummaryVisitedRegionUiState("2위", "강북구")
            )
        )
    )
}

private fun previewBars(): List<WeeklySummaryBarUiState> {
    return listOf(
        WeeklySummaryBarUiState(ratio = 0.88f, isHighlighted = false, hasData = true),
        WeeklySummaryBarUiState(ratio = 0.62f, isHighlighted = false, hasData = true),
        WeeklySummaryBarUiState(ratio = 0.70f, isHighlighted = false, hasData = true),
        WeeklySummaryBarUiState(ratio = 0.38f, isHighlighted = false, hasData = true),
        WeeklySummaryBarUiState(ratio = 0.78f, isHighlighted = false, hasData = true),
        WeeklySummaryBarUiState(ratio = 0.50f, isHighlighted = false, hasData = true),
        WeeklySummaryBarUiState(ratio = 0.70f, isHighlighted = true, hasData = true)
    )
}
