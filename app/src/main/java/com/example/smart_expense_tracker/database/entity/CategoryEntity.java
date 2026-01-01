package com.example.smart_expense_tracker.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * ② 分类表 (categories)
 * 管理支出的类别。
 *
 * id: 主键 (INTEGER)
 * name: 类别名（如：餐饮、交通、购物）
 * type: 类型 (INTEGER, 0:支出, 1:收入)
 * icon_res: 图标资源 ID 或名称 (TEXT)
 */
@Entity(tableName = "categories")
public class CategoryEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "type")
    public int type = 0; // 0:支出, 1:收入

    @ColumnInfo(name = "icon_res")
    public String iconRes;
}
