package com.example.smart_expense_tracker.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * ④ 交易记录表 (transactions)
 * 存储每一笔具体的账单，这是 AI 分析的数据来源。
 *
 * id: 主键 (INTEGER)
 * book_id: 外键，关联账本表 (INTEGER)
 * category_id: 外键，关联分类表 (INTEGER)
 * account_id: 外键，关联账户表 (INTEGER)
 * amount: 金额 (INTEGER，单位：分)
 * type: 交易类型 (INTEGER, 0:支出, 1:收入, 2:转账)
 * record_time: 交易发生时间 (INTEGER，存储时间戳)
 * remark: 备注 (TEXT)
 */
@Entity(
    tableName = "transactions",
    foreignKeys = {
        @ForeignKey(entity = BookEntity.class, parentColumns = "id", childColumns = "book_id"),
        @ForeignKey(entity = CategoryEntity.class, parentColumns = "id", childColumns = "category_id"),
        @ForeignKey(entity = AccountEntity.class, parentColumns = "id", childColumns = "account_id")
    },
        indices = {
                @Index("record_time"),
                @Index("book_id"),      // 新增 book_id 的索引
                @Index("category_id"),  // 新增 category_id 的索引
                @Index("account_id")    // 新增 account_id 的索引，修复警告
        }
)
public class TransactionEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "book_id")
    public Integer bookId;

    @ColumnInfo(name = "category_id")
    public Integer categoryId;

    @ColumnInfo(name = "account_id")
    public Integer accountId;

    @ColumnInfo(name = "amount")
    public long amount;

    @ColumnInfo(name = "type")
    public int type; // 0:支出, 1:收入, 2:转账

    @ColumnInfo(name = "record_time")
    public long recordTime;

    @ColumnInfo(name = "remark")
    public String remark;
}
