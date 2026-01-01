package com.example.smart_expense_tracker.ui.screens

import android.app.Application
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smart_expense_tracker.database.entity.CategoryEntity
import com.example.smart_expense_tracker.repository.MonthlyStats
import com.example.smart_expense_tracker.ui.components.*
import com.example.smart_expense_tracker.viewmodel.StatisticsViewModel
import com.example.smart_expense_tracker.viewmodel.StatisticsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    context: Context = LocalContext.current,
) {
    val application = context.applicationContext as Application
    val viewModel: StatisticsViewModel = viewModel(factory = StatisticsViewModelFactory(application))

    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val monthlyStats by viewModel.monthlyStats.collectAsState()
    val categoryData by viewModel.categoryData.collectAsState()
    val trendData by viewModel.trendData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(selectedPeriod) {
        viewModel.setPeriod(selectedPeriod)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("消费统计") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "返回") } },
                actions = { IconButton(onClick = { viewModel.setPeriod(selectedPeriod) }) { Icon(Icons.Default.Refresh, contentDescription = "刷新") } },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        if (error != null) {
            // Error state UI
        } else if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item { TimePeriodSelector(selectedPeriod) { viewModel.setPeriod(it) } }
                item { PeriodTitle(selectedPeriod, monthlyStats) }
                item { ExpenseOverviewCard(monthlyStats) }
                item { CategoryPieChartCard(categoryData) }
                item { ExpenseTrendCard(selectedPeriod, trendData) }
                item { CategoryDetailsCard(categoryData) }
            }
        }
    }
}

@Composable
private fun TimePeriodSelector(selectedPeriod: Int, onPeriodSelected: (Int) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(Modifier.fillMaxWidth().padding(8.dp), Arrangement.spacedBy(8.dp)) {
            TimePeriodButton("本周", selectedPeriod == 0, { onPeriodSelected(0) }, Modifier.weight(1f))
            TimePeriodButton("本月", selectedPeriod == 1, { onPeriodSelected(1) }, Modifier.weight(1f))
            TimePeriodButton("本年", selectedPeriod == 2, { onPeriodSelected(2) }, Modifier.weight(1f))
        }
    }
}

@Composable
private fun TimePeriodButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick, modifier = modifier,
        colors = ButtonDefaults.buttonColors(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
        shape = CircleShape,
    ) {
        Text(text, color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun PeriodTitle(selectedPeriod: Int, monthlyStats: MonthlyStats?) {
    val title = when (selectedPeriod) {
        0 -> if (monthlyStats != null) "${monthlyStats.year}年第${monthlyStats.month}周" else "本周"
        2 -> if (monthlyStats != null) "${monthlyStats.year}年" else "本年"
        else -> if (monthlyStats != null) "${monthlyStats.year}年${monthlyStats.month}月" else "本月"
    }
    Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
}

@Composable
private fun ExpenseOverviewCard(monthlyStats: MonthlyStats?) {
    val totalExpense = monthlyStats?.expense ?: 0
    val totalIncome = monthlyStats?.income ?: 0
    val balance = monthlyStats?.balance ?: 0

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("消费概览", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                ExpenseSummaryItem("支出", "¥${totalExpense / 100.0}", MaterialTheme.colorScheme.error, Icons.Default.TrendingDown)
                ExpenseSummaryItem("收入", "¥${totalIncome / 100.0}", MaterialTheme.colorScheme.primary, Icons.Default.TrendingUp)
                val balanceColor = if (balance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                ExpenseSummaryItem("结余", "¥${balance / 100.0}", balanceColor, Icons.Default.AccountBalance)
            }
        }
    }
}

@Composable
private fun ExpenseSummaryItem(label: String, value: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun CategoryPieChartCard(categoryData: Map<CategoryEntity, Long>) {
    fun nameToColor(name: String): Color {
        val hash = name.hashCode()
        return Color((0xFF shl 24) or (hash and 0xFFFFFF))
    }
    val pieChartData = categoryData.map {
        PieChartData(it.key.name ?: "", it.value.toDouble() / 100.0, nameToColor(it.key.name ?: ""))
    }
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("分类占比", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (pieChartData.isEmpty()) {
                Text("暂无数据", Modifier.align(Alignment.CenterHorizontally))
            } else {
                PieChartWithLegend(pieChartData, Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun ExpenseTrendCard(selectedPeriod: Int, trendData: List<Float>) {
    val chartData = trendData.mapIndexed { index, value ->
        val label = when (selectedPeriod) {
            0 -> "第${index + 1}天"
            1 -> "${index + 1}日"
            2 -> "${index + 1}月"
            else -> ""
        }
        BarChartData(label, value, MaterialTheme.colorScheme.primary)
    }
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("支出趋势", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (chartData.isEmpty()) {
                Text("暂无数据", Modifier.align(Alignment.CenterHorizontally))
            } else {
                when (selectedPeriod) {
                    0 -> WeeklyBarChart(chartData, Modifier.fillMaxWidth())
                    1 -> MonthlyBarChart(chartData, Modifier.fillMaxWidth())
                    2 -> YearlyBarChart(chartData, Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun CategoryDetailsCard(categoryData: Map<CategoryEntity, Long>) {
    val total = categoryData.values.sum().takeIf { it > 0 } ?: 1L
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("分类详情", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            categoryData.entries.sortedByDescending { it.value }.forEach { (category, amount) ->
                CategoryDetailItem(category.name ?: "", (amount / 100).toInt(), amount.toFloat() / total.toFloat())
            }
        }
    }
}

@Composable
private fun CategoryDetailItem(category: String, amount: Int, percentage: Float) {
    val color = when (category) {
        "餐饮" -> Color(0xFFFF7043)
        "购物" -> Color(0xFF42A5F5)
        "交通" -> Color(0xFF66BB6A)
        else -> Color(0xFFAB47BC)
    }
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.size(16.dp).background(color, CircleShape))
            Text(category, style = MaterialTheme.typography.bodyMedium)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("¥$amount", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text("${(percentage * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
        }
    }
}
