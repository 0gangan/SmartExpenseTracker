package com.example.smart_expense_tracker.repository

import com.example.smart_expense_tracker.database.entity.CategoryEntity

data class CategoryStats(
    val category: CategoryEntity,
    val total: Long
)
