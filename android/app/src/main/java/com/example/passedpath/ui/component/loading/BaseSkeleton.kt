package com.example.passedpath.ui.component.loading

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.passedpath.ui.theme.Gray100
import com.example.passedpath.ui.theme.Gray50
import com.example.passedpath.ui.theme.PassedPathTheme
import com.example.passedpath.ui.theme.White

@Composable
fun rememberBaseSkeletonBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "base_skeleton")
    val shimmerOffset by transition.animateFloat(
        initialValue = -260f,
        targetValue = 760f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_450, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "base_skeleton_offset"
    )

    return Brush.linearGradient(
        colors = listOf(
            Gray100,
            Gray50,
            White.copy(alpha = 0.62f),
            Gray50,
            Gray100
        ),
        start = Offset(shimmerOffset, 0f),
        end = Offset(shimmerOffset + 180f, 0f)
    )
}

@Composable
fun BaseSkeletonBlock(
    brush: Brush,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    Box(
        modifier = modifier.background(
            brush = brush,
            shape = shape
        )
    )
}

@Preview(showBackground = true, name = "Base Skeleton")
@Composable
private fun BaseSkeletonPreview() {
    PassedPathTheme {
        val skeletonBrush = rememberBaseSkeletonBrush()

        Column(
            modifier = Modifier
                .background(White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BaseSkeletonBlock(
                brush = skeletonBrush,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp)
            )
            BaseSkeletonBlock(
                brush = skeletonBrush,
                modifier = Modifier
                    .width(180.dp)
                    .height(14.dp)
            )
            BaseSkeletonBlock(
                brush = skeletonBrush,
                modifier = Modifier
                    .width(120.dp)
                    .height(12.dp)
            )
        }
    }
}
