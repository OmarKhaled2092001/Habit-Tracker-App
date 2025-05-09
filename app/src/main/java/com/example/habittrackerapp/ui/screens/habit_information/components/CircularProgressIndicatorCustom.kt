package com.example.habittrackerapp.ui.screens.habit_information.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun CircularProgressIndicatorCustom(
    modifier: Modifier = Modifier,
    progress: Float,
    strokeWidth: Dp,
    backgroundColor: Color,
    foregroundColor: Color
) {
    val sweepAngle = 360 * progress.coerceIn(0f, 1f)

    Canvas(modifier = modifier) {
        val diameter = size.minDimension
        val topLeftOffset = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
        val strokePx = strokeWidth.toPx()
        val arcSize = Size(diameter - strokePx, diameter - strokePx)
        val arcTopLeft = topLeftOffset + Offset(strokePx / 2f, strokePx / 2f)

        // Background Arc
        drawArc(
            color = backgroundColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )

        // Foreground Arc (Progress)
        if (sweepAngle > 0f) {
            drawArc(
                color = foregroundColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
    }
}



@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun CircularProgressIndicatorPreview() {
    CircularProgressIndicatorCustom(
        modifier = Modifier.size(100.dp),
        progress = 0.75f,
        strokeWidth = 8.dp,
        backgroundColor = Color.Gray,
        foregroundColor = Color.Blue
    )
}