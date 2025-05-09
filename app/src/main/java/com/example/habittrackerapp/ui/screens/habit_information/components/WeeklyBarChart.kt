package com.example.habittrackerapp.ui.screens.habit_information.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.InsertChartOutlined
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittrackerapp.ui.screens.habit_information.HabitInformationColors
import com.example.habittrackerapp.ui.screens.habit_information.generateHabitInformationColors
import kotlin.math.ceil

private const val GRID_LINE_COUNT = 4
private val DAYS_OF_WEEK = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

@Composable
fun WeeklyBarChart(
    data: List<Float>,
    highlightIndex: Int,
    highlightValue: String,
    colors: HabitInformationColors,
    modifier: Modifier = Modifier,
    barChartHeight: Dp = 150.dp // Default height, can be overridden
) {
    if (data.isEmpty()) {
        EmptyChartView(modifier = modifier.height(barChartHeight), colors = colors)
        return
    }

    val textMeasurer = rememberTextMeasurer()
    val labelTextStyle = TextStyle(fontSize = 10.sp, color = Color.Gray)

    val actualMaxData = data.maxOrNull() ?: 0f
    val yAxisTopValue = if (actualMaxData == 0f) {
        GRID_LINE_COUNT.toFloat()
    } else {
        val step = ceil(actualMaxData / GRID_LINE_COUNT.toFloat()).toFloat()
        (step * GRID_LINE_COUNT).coerceAtLeast(GRID_LINE_COUNT.toFloat())
    }

    BoxWithConstraints(modifier = modifier) {
        val canvasHeight = maxOf(barChartHeight, this.maxHeight * 0.8f)

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(canvasHeight)
        ) {
            val tooltipReservedSpaceOnCanvasTop = 30.dp.toPx()
            val yAxisLabelWidth = textMeasurer.measure(
                AnnotatedString(yAxisTopValue.toInt().toString()),
                style = labelTextStyle
            ).size.width.toFloat() + 8.dp.toPx()
            val xAxisLabelHeight = 20.dp.toPx()

            val chartAreaWidth = size.width - yAxisLabelWidth
            val chartAreaHeight = size.height - xAxisLabelHeight - tooltipReservedSpaceOnCanvasTop

            if (chartAreaHeight <= 0f || chartAreaWidth <= 0f) return@Canvas

            drawGridLinesAndYAxisLabels(
                drawScope = this,
                textMeasurer = textMeasurer,
                labelTextStyle = labelTextStyle,
                yAxisTopValue = yAxisTopValue,
                chartAreaHeight = chartAreaHeight,
                chartAreaWidth = chartAreaWidth, // Pass chartAreaWidth
                yAxisLabelWidth = yAxisLabelWidth,
                tooltipReservedSpaceOnCanvasTop = tooltipReservedSpaceOnCanvasTop
            )

            drawBarsAndXAxisLabels(
                drawScope = this,
                textMeasurer = textMeasurer,
                labelTextStyle = labelTextStyle,
                data = data,
                colors = colors,
                highlightIndex = highlightIndex,
                highlightValue = highlightValue,
                yAxisTopValue = yAxisTopValue,
                chartAreaHeight = chartAreaHeight,
                chartAreaWidth = chartAreaWidth,
                yAxisLabelWidth = yAxisLabelWidth,
                tooltipReservedSpaceOnCanvasTop = tooltipReservedSpaceOnCanvasTop
            )
        }
    }
}

@Composable
private fun EmptyChartView(modifier: Modifier = Modifier, colors: HabitInformationColors) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.InsertChartOutlined,
                contentDescription = "No data available",
                modifier = Modifier.size(48.dp),
                tint = colors.primary.copy(alpha = 0.7f) // Use color from theme
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No data available",
                fontSize = 16.sp,
                color = colors.primary.copy(alpha = 0.9f), // Use color from theme
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun drawGridLinesAndYAxisLabels(
    drawScope: DrawScope,
    textMeasurer: TextMeasurer,
    labelTextStyle: TextStyle,
    yAxisTopValue: Float,
    chartAreaHeight: Float,
    chartAreaWidth: Float,
    yAxisLabelWidth: Float,
    tooltipReservedSpaceOnCanvasTop: Float
) {
    with(drawScope) {
        val dashedGridLinePathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
        val gridValues = List(GRID_LINE_COUNT + 1) { i ->
            (yAxisTopValue / GRID_LINE_COUNT * i)
        }

        gridValues.forEach { value ->
            val y = tooltipReservedSpaceOnCanvasTop + (chartAreaHeight - (value / yAxisTopValue * chartAreaHeight))
            drawLine(
                color = Color(0xFFE0E0E0),
                start = Offset(yAxisLabelWidth, y),
                end = Offset(yAxisLabelWidth + chartAreaWidth, y),
                strokeWidth = 1f,
                pathEffect = if (value != 0f) dashedGridLinePathEffect else null
            )
            val labelLayoutResult = textMeasurer.measure(AnnotatedString(value.toInt().toString()), style = labelTextStyle)
            drawText(
                textLayoutResult = labelLayoutResult,
                topLeft = Offset(
                    (yAxisLabelWidth - labelLayoutResult.size.width - 4.dp.toPx()).coerceAtLeast(0f),
                    y - labelLayoutResult.size.height / 2f
                )
            )
        }
    }
}

private fun drawBarsAndXAxisLabels(
    drawScope: DrawScope,
    textMeasurer: TextMeasurer,
    labelTextStyle: TextStyle,
    data: List<Float>,
    colors: HabitInformationColors,
    highlightIndex: Int,
    highlightValue: String,
    yAxisTopValue: Float,
    chartAreaHeight: Float,
    chartAreaWidth: Float,
    yAxisLabelWidth: Float,
    tooltipReservedSpaceOnCanvasTop: Float
) {
    with(drawScope) {
        val barCount = data.size
        val barSpacingRatio = if (barCount > 7) 0.3f else 0.4f
        val totalBarWidth = chartAreaWidth * (1 - barSpacingRatio)
        val barWidth = (totalBarWidth / barCount).coerceAtLeast(1.dp.toPx())
        val barSpacing = (chartAreaWidth * barSpacingRatio) / (barCount + 1)

        data.forEachIndexed { index, value ->
            val barHeight = (value / yAxisTopValue * chartAreaHeight).coerceAtLeast(0f)
            val left = yAxisLabelWidth + barSpacing + index * (barWidth + barSpacing)
            val top = tooltipReservedSpaceOnCanvasTop + (chartAreaHeight - barHeight)
            val isHighlighted = index == highlightIndex
            val barColor = if (isHighlighted) colors.primary else colors.primaryLight

            drawRoundRect(
                color = barColor,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight.coerceAtLeast(0f)),
                cornerRadius = CornerRadius(x = 4.dp.toPx(), y = 4.dp.toPx())
            )

            if (index < DAYS_OF_WEEK.size) {
                val dayLabelLayoutResult = textMeasurer.measure(AnnotatedString(DAYS_OF_WEEK[index]), style = labelTextStyle)
                drawText(
                    textLayoutResult = dayLabelLayoutResult,
                    topLeft = Offset(
                        left + barWidth / 2f - dayLabelLayoutResult.size.width / 2f,
                        tooltipReservedSpaceOnCanvasTop + chartAreaHeight + 4.dp.toPx()
                    )
                )
            }

            if (isHighlighted) {
                drawTooltip(
                    scope = this,
                    textMeasurer = textMeasurer,
                    text = highlightValue,
                    barTopY = top,
                    barCenterX = left + barWidth / 2f,
                    colors = colors,
                    canvasTopY = 0f
                )
            }
        }
    }
}

private fun drawTooltip(
    scope: DrawScope,
    textMeasurer: TextMeasurer,
    text: String,
    barTopY: Float,
    barCenterX: Float,
    colors: HabitInformationColors,
    canvasTopY: Float
) {
    with(scope) {
        val textStyle = TextStyle(color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        val textLayoutResult = textMeasurer.measure(AnnotatedString(text), style = textStyle)
        val paddingHorizontal = 8.dp.toPx()
        val paddingVertical = 4.dp.toPx()
        val tooltipWidth = textLayoutResult.size.width + paddingHorizontal * 2
        val tooltipHeight = textLayoutResult.size.height + paddingVertical * 2
        val triangleSize = 6.dp.toPx()
        val tooltipGap = 4.dp.toPx()

        val preferredTooltipY = barTopY - tooltipHeight - triangleSize - tooltipGap
        val tooltipY = preferredTooltipY.coerceAtLeast(canvasTopY)
        val tooltipX = (barCenterX - tooltipWidth / 2f).coerceIn(0f, size.width - tooltipWidth)
        val tooltipBackgroundColor = colors.primary

        drawRoundRect(
            color = tooltipBackgroundColor,
            topLeft = Offset(tooltipX, tooltipY),
            size = Size(tooltipWidth, tooltipHeight),
            cornerRadius = CornerRadius(4.dp.toPx())
        )

        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(tooltipX + paddingHorizontal, tooltipY + paddingVertical)
        )

        val triangleBaseY = tooltipY + tooltipHeight
        val triangleTipTargetY = barTopY - tooltipGap
        val triangleTipY = triangleTipTargetY.coerceAtMost(triangleBaseY + triangleSize * 0.8f).coerceAtLeast(triangleBaseY)

        val trianglePath = androidx.compose.ui.graphics.Path().apply {
            moveTo(barCenterX, triangleTipY)
            lineTo(barCenterX - triangleSize / 2f, triangleBaseY)
            lineTo(barCenterX + triangleSize / 2f, triangleBaseY)
            close()
        }
        drawPath(path = trianglePath, color = tooltipBackgroundColor)
    }
}

@Preview(showBackground = true, name = "Standard Data")
@Composable
private fun WeeklyBarChartPreview() {
    WeeklyBarChart(
        data = listOf(1f, 2f, 10f, 4f, 5f, 0f, 7f),
        highlightIndex = 2,
        highlightValue = "10 units",
        colors = generateHabitInformationColors(Color(0xFF2196F3)),
        modifier = Modifier.height(250.dp)
    )
}

@Preview(showBackground = true, name = "Empty Data")
@Composable
fun WeeklyBarChartEmptyPreview() {
    WeeklyBarChart(
        data = emptyList(),
        highlightIndex = 0,
        highlightValue = "",
        colors = generateHabitInformationColors(Color(0xFF2196F3)),
        modifier = Modifier.fillMaxWidth().height(250.dp)
    )
}

@Preview(showBackground = true, name = "All Zero Data")
@Composable
private fun WeeklyBarChartAllZeroPreview() {
    WeeklyBarChart(
        data = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f),
        highlightIndex = 0,
        highlightValue = "0 units",
        colors = generateHabitInformationColors(Color(0xFF2196F3)),
        modifier = Modifier.height(250.dp)
    )
}

@Preview(showBackground = true, name = "Tall Bar Data")
@Composable
private fun WeeklyBarChartTallBarPreview() {
    WeeklyBarChart(
        data = listOf(6f, 3f, 6f, 5f, 50f, 2f, 7f),
        highlightIndex = 4,
        highlightValue = "50 min",
        colors = generateHabitInformationColors(Color(0xFF4CAF50)),
        modifier = Modifier.fillMaxWidth().height(250.dp)
    )
}

