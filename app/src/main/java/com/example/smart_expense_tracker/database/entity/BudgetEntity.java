package com.example.smart_expense_tracker.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "budget")
public class BudgetEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String category;
    public long budgetAmount;
    public long spentAmount;
    public int year;
    public int month;
    public String note;
    public long createTime;
    public long updateTime;
    
    public BudgetEntity() {
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }
    
    @Ignore
    public BudgetEntity(String category, long budgetAmount, int year, int month) {
        this();
        this.category = category;
        this.budgetAmount = budgetAmount;
        this.year = year;
        this.month = month;
    }
    
    // 计算剩余预算
    public long getRemainingBudget() {
        return budgetAmount - spentAmount;
    }
    
    // 计算预算使用百分比
    public float getUsagePercentage() {
        if (budgetAmount <= 0) return 0f;
        return (float) spentAmount / budgetAmount;
    }
    
    // 是否超预算
    public boolean isOverBudget() {
        return spentAmount > budgetAmount;
    }
}