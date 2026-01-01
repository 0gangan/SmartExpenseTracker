package com.example.smart_expense_tracker.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * ③ 账户表 (accounts)
 * 管理资金来源。
 *
 * id: 主键 (INTEGER)
 * name: 账户名（如：支付宝、现金、招行卡）
 * balance: 余额 (INTEGER，建议以"分"为单位存储，避免浮点数精度问题)
 * color: 账户图标颜色 (INTEGER，用于UI显示)
 */
@Entity(tableName = "accounts")
public class AccountEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "balance")
    public long balance = 0;

    @ColumnInfo(name = "color")
    public int color = 0; // 默认颜色值
}
