package com.example.passedpath.feature.summary.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.passedpath.R
import com.example.passedpath.feature.summary.presentation.component.DaySummaryMetricCard
import com.example.passedpath.feature.summary.presentation.state.DaySummaryContentUiState
import com.example.passedpath.feature.summary.presentation.state.DaySummaryUiState
import com.example.passedpath.ui.theme.Gray100
import com.example.passedpath.ui.theme.Gray400
import com.example.passedpath.ui.theme.Gray900
import com.example.passedpath.ui.theme.Green500
import com.example.passedpath.ui.theme.PassedPathTheme

@Composable
fun DaySummaryBottomSheetContent(
    selectedDateKey: String,
    uiState: DaySummaryUiState,
    onLoadSummary: (String) -> Unit,
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

    LaunchedEffect(selectedDateKey, uiState.dateKey, uiState.hasLoaded, uiState.isLoading) {
        if (uiState.dateKey != selectedDateKey ||
            (!uiState.hasLoaded && !uiState.isLoading && uiState.errorMessage == null)
        ) {
            onLoadSummary(selectedDateKey)
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

        if (uiState.errorMessage != null) {
            item {
                DaySummaryErrorNotice(
                    message = uiState.errorMessage,
                    onRetryClick = onRetryClick
                )
            }
        } else {
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
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun DaySummaryErrorNotice(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Gray100, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.day_summary_error_title),
            style = MaterialTheme.typography.bodyMedium,
            color = Gray900,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = Gray400
        )
        TextButton(
            onClick = onRetryClick,
            modifier = Modifier.height(32.dp)
        ) {
            Text(
                text = stringResource(R.string.route_retry),
                color = Green500,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun DaySummaryBottomSheetContentPreview() {
    PassedPathTheme {
        DaySummaryBottomSheetContent(
            selectedDateKey = "2026-05-05",
            uiState = DaySummaryUiState(
                dateKey = "2026-05-05",
                hasLoaded = true,
                summary = DaySummaryContentUiState(
                    outingTimeText = "09:12",
                    enterHomeTimeText = "21:03",
                    totalOutingDurationText = "11h 51m",
                    totalOutingCountText = "3"
                )
            ),
            onLoadSummary = {},
            onRetryClick = {},
            onScrollStateChanged = {}
        )
    }
}
