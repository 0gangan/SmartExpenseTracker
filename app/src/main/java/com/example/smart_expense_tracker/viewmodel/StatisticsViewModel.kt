package com.example.smart_expense_tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_expense_tracker.database.entity.CategoryEntity
import com.example.smart_expense_tracker.repository.ExpenseRepository
import com.example.smart_expense_tracker.repository.MonthlyStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ExpenseRepository.getInstance(application)

    private val _selectedPeriod = MutableStateFlow(1) // 0: 周, 1: 月, 2: 年
    val selectedPeriod: StateFlow<Int> = _selectedPeriod.asStateFlow()

    private val _monthlyStats = MutableStateFlow<MonthlyStats?>(null)
    val monthlyStats: StateFlow<MonthlyStats?> = _monthlyStats.asStateFlow()

    private val _categoryData = MutableStateFlow<Map<CategoryEntity, Long>>(emptyMap())
    val categoryData: StateFlow<Map<CategoryEntity, Long>> = _categoryData.asStateFlow()

    private val _trendData = MutableStateFlow<List<Float>>(emptyList())
    val trendData: StateFlow<List<Float>> = _trendData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadStatistics()
    }

    fun setPeriod(period: Int) {
        _selectedPeriod.value = period
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1
                when (_selectedPeriod.value) {
                    0 -> loadWeeklyStats()
                    1 -> loadMonthlyStats(year, month)
                    2 -> loadYearlyStats(year)
                }
            } catch (e: Exception) {
                _error.value = "加载统计数据失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadWeeklyStats() {
        val calendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        calendar.add(Calendar.DATE, 7)
        val endTime = calendar.timeInMillis - 1

        val expense = repository.getTotalExpense(startTime, endTime)
        val income = repository.getTotalIncome(startTime, endTime)
        val categoryStats = repository.getCategoryStatistics(startTime, endTime)

        _monthlyStats.value = MonthlyStats(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR), expense, income, income - expense, categoryStats)
        loadCategoryData(categoryStats)

        val trendValues = MutableList(7) { 0f }
        repository.getTransactionsByPeriod(startTime, endTime)
            .filter { it.type == 0 } // 只统计支出
            .forEach { tx ->
                val dayOfWeek = ((tx.recordTime - startTime) / (24 * 60 * 60 * 1000)).toInt()
                if (dayOfWeek in 0..6) {
                    trendValues[dayOfWeek] += tx.amount / 100f
                }
            }
        _trendData.value = trendValues
    }

    private suspend fun loadMonthlyStats(year: Int, month: Int) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
        }
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val trendValues = MutableList(maxDay) { 0f }

        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startTime = cal.timeInMillis

        cal.add(Calendar.MONTH, 1)
        val endTime = cal.timeInMillis - 1

        repository.getTransactionsByPeriod(startTime, endTime)
            .filter { it.type == 0 } // 只统计支出
            .forEach { tx ->
                val txCal = Calendar.getInstance().apply { timeInMillis = tx.recordTime }
                val dayOfMonth = txCal.get(Calendar.DAY_OF_MONTH) - 1
                if (dayOfMonth in 0 until maxDay) {
                    trendValues[dayOfMonth] += tx.amount / 100f
                }
            }
        _trendData.value = trendValues

        val stats = repository.getMonthlyStats(year, month)
        _monthlyStats.value = stats
        loadCategoryData(stats.categoryStats)
    }

    private suspend fun loadYearlyStats(year: Int) {
        val monthlyValues = (0..11).map { monthIndex -> // Corrected the loop range
            val cal = Calendar.getInstance().apply {
                set(year, monthIndex, 1, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startTime = cal.timeInMillis

            cal.add(Calendar.MONTH, 1)
            val endTime = cal.timeInMillis - 1

            repository.getTotalExpense(startTime, endTime) / 100f
        }

        _trendData.value = monthlyValues

        val startOfYear = Calendar.getInstance().apply { set(year, 0, 1, 0, 0, 0) }.timeInMillis
        val endOfYear = Calendar.getInstance().apply { set(year, 11, 31, 23, 59, 59) }.timeInMillis
        val expense = repository.getTotalExpense(startOfYear, endOfYear)
        val income = repository.getTotalIncome(startOfYear, endOfYear)
        val categoryStats = repository.getCategoryStatistics(startOfYear, endOfYear)

        _monthlyStats.value = MonthlyStats(year, 12, expense, income, income - expense, categoryStats)
        loadCategoryData(categoryStats)
    }

    private suspend fun loadCategoryData(categoryStats: Map<Int, Long>) {
        val allCategories = repository.getAllCategories().associateBy { it.id }
        _categoryData.value = categoryStats.mapNotNull { (categoryId, amount) ->
            allCategories[categoryId]?.let { category ->
                category to amount
            }
        }.toMap()
    }

    fun clearError() {
        _error.value = null
    }
}