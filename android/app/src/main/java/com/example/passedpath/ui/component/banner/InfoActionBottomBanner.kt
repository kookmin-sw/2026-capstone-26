package com.example.passedpath.ui.component.banner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.passedpath.R
import com.example.passedpath.ui.theme.Gray100
import com.example.passedpath.ui.theme.Green100

@Composable
fun InfoActionBottomBanner(
    message: String,
    actionText: String,
    onClickAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconActionBottomBanner(
        message = message,
        actionText = actionText,
        onClickAction = onClickAction,
        modifier = modifier,
        iconResId = R.drawable.ic_information,
        cornerRadius = 13.dp,
        iconTint = Color.Unspecified,
        borderColor = Green100
    )
}

@Preview(showBackground = true, name = "Info Action Bottom Banner")
@Composable
private fun InfoActionBottomBannerPreview() {
    com.example.passedpath.ui.theme.PassedPathTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF3F4F6))
                .padding(16.dp)
        ) {
            InfoActionBottomBanner(
                message = "Long press cards to reorder visits.",
                actionText = "Close",
                onClickAction = {}
            )
        }
    }
}
