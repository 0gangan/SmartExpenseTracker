package com.example.smart_expense_tracker.model

// 账户类型枚举
enum class AccountType(val displayName: String) {
    ASSET("资产"),
    LIABILITY("负债")
}

// 账户项目数据模型
data class AccountItem(
    val id: Long = 0,
    val type: AccountType,
    val name: String,
    val amount: Double,
    val category: String,
    val color: Int = 0 // 账户图标颜色
)