package com.example.passedpath.ui.component.feedback

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passedpath.R
import com.example.passedpath.ui.theme.Gray300
import com.example.passedpath.ui.theme.Gray50
import com.example.passedpath.ui.theme.Gray500
import com.example.passedpath.ui.theme.Green500
import com.example.passedpath.ui.theme.PassedPathTheme

@Composable
fun WifiFailurePanel(
    title: String,
    message: String,
    retryText: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    textAlign: TextAlign = TextAlign.Start
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Gray50,
        border = BorderStroke(1.dp, Gray300),
        tonalElevation = 0.dp,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_info_circle),
                contentDescription = title,
                tint = Gray500,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = message,
                    color = Gray500,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = textAlign,
                    softWrap = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = retryText,
                    modifier = Modifier
                        .clickable(onClick = onRetryClick)
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    color = Green500,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF4B5563)
@Composable
private fun WifiFailurePanelPreview() {
    PassedPathTheme {
        WifiFailurePanel(
            title = "Network notice",
            message = "선택 옵션에 관계없이, 언제든지 직접 데이터를 삭제할 수 있습니다.",
            retryText = "다시 시도",
            onRetryClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
