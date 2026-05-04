package com.example.passedpath.feature.summary.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passedpath.R
import com.example.passedpath.ui.theme.Black
import com.example.passedpath.ui.theme.Gray400
import com.example.passedpath.ui.theme.Gray50
import com.example.passedpath.ui.theme.Green500
import com.example.passedpath.ui.theme.PassedPathTheme

@Composable
fun DaySummaryMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(color = Gray50, shape = RoundedCornerShape(18.dp))
            .padding(horizontal = 22.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Green500,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = Gray400,
            modifier = Modifier.size(width = 7.dp, height = 12.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun DaySummaryMetricCardPreview() {
    PassedPathTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DaySummaryMetricCard(label = "외출 시간", value = "09:12")
            DaySummaryMetricCard(label = "귀가 시간", value = "21:03")
            DaySummaryMetricCard(label = "총 외출 시간", value = "11시간 51분")
            DaySummaryMetricCard(label = "외출 횟수", value = "3회")
            DaySummaryMetricCard(label = "방문한 동네 리스트", value = "성북구 · 강북구 · 종로구")
        }
    }
}
