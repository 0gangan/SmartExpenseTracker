package com.example.smart_expense_tracker.ui.components

import androidx.compose.ui.graphics.Color

/**
 * Data class for a single bar in the bar chart.
 * This is in a separate file to be shared by all chart components.
 */
data class BarChartData(
    val label: String,
    val value: Float,
    val color: Color
)
