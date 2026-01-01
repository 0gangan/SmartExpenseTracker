package com.example.smart_expense_tracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun YearlyBarChart(
    data: List<BarChartData>,
    modifier: Modifier = Modifier
) {
    val barWidth = 20.dp
    val barSpacing = 16.dp
    val chartHeight = 200.dp

    val maxValue = (data.maxOfOrNull { it.value } ?: 0f).let { if (it == 0f) 100f else it } * 1.2f
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animationProgress.animateTo(1f, tween(durationMillis = 1000))
    }

    val contentWidth = remember(data.size, barWidth, barSpacing) {
        (data.size * (barWidth.value + barSpacing.value) + barSpacing.value * 2).dp
    }
    val textMeasurer = rememberTextMeasurer()
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(chartHeight + 40.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        Canvas(
            modifier = Modifier
                .width(contentWidth)
                .fillMaxHeight()
                .padding(horizontal = 8.dp)
        ) {
            val yAxisLabelWidth = 40.dp.toPx()
            val xAxisLabelHeight = 30.dp.toPx()
            val canvasChartHeight = size.height - xAxisLabelHeight

            if (data.isEmpty()) return@Canvas

            val gridLineCount = 4
            (0..gridLineCount).forEach { i ->
                val y = canvasChartHeight - (canvasChartHeight / gridLineCount * i)
                drawLine(
                    color = colorScheme.outline.copy(alpha = 0.2f),
                    start = Offset(yAxisLabelWidth, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
                val value = maxValue / gridLineCount * i
                val textResult = textMeasurer.measure(
                    String.format(Locale.getDefault(), "%.0f", value),
                    style = TextStyle(color = colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 10.sp)
                )
                drawText(
                    textResult,
                    topLeft = Offset(yAxisLabelWidth - textResult.size.width - 4.dp.toPx(), y - textResult.size.height / 2)
                )
            }

            data.forEachIndexed { index, barData ->
                val barSlotWidth = barWidth.toPx() + barSpacing.toPx()
                val barStartX = yAxisLabelWidth + barSlotWidth * index + barSpacing.toPx() / 2f

                val barHeight = (barData.value / maxValue) * canvasChartHeight * animationProgress.value
                val barTopY = canvasChartHeight - barHeight

                drawRoundRect(
                    color = barData.color,
                    topLeft = Offset(barStartX, barTopY),
                    size = Size(barWidth.toPx(), barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )

                val valueText = String.format(Locale.getDefault(), "%.2f", barData.value)
                val valueTextResult = textMeasurer.measure(
                    text = valueText, style = TextStyle(color = colorScheme.onSurface, fontSize = 10.sp)
                )
                if (barData.value > 0) {
                    drawText(
                        valueTextResult,
                        topLeft = Offset(barStartX + barWidth.toPx() / 2 - valueTextResult.size.width / 2, barTopY - valueTextResult.size.height - 4.dp.toPx())
                    )
                }

                val labelTextResult = textMeasurer.measure(
                    text = barData.label, style = TextStyle(color = colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 10.sp)
                )
                drawText(
                    labelTextResult,
                    topLeft = Offset(barStartX + barWidth.toPx() / 2 - labelTextResult.size.width / 2, canvasChartHeight + 8.dp.toPx())
                )
            }
        }
    }
}