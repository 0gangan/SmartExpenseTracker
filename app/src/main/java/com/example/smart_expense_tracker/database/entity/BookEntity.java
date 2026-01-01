package com.example.smart_expense_tracker.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * ① 账本表 (books)
 * 支持多账本，如“个人账”、“工作账”。
 *
 * id: 主键 (INTEGER)
 * name: 账本名称 (TEXT)
 * is_default: 是否默认 (INTEGER, 1表示是)
 */
@Entity(tableName = "books")
public class BookEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "is_default")
    public int isDefault = 0;
}
