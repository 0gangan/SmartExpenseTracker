package com.example.smart_expense_tracker.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * ⑤ AI 报告表 (ai_reports)
 * 存储 DeepSeek 返回的分析结果，实现周报、月报、年报的缓存。
 *
 * id: 主键 (INTEGER)
 * report_type: 报告周期 (TEXT，值为 'WEEK', 'MONTH', 'YEAR')
 * report_date: 时间标识 (TEXT，如 '2023-W42', '2023-10', '2023')
 * content: AI 返回的分析文本 (TEXT)
 * data_hash: 数据校验特征值 (TEXT)。用于记录生成此报告时的流水特征，
   如果用户修改了账单，Hash 不匹配，则提示用户“数据已更新，需重新生成分析”。
 * update_time: 最后生成时间 (INTEGER)
 */
@Entity(
    tableName = "ai_reports",
    indices = {@Index(value = {"report_type", "report_date"}, unique = true)}
)
public class AiReportEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "report_type")
    public String reportType; // 'WEEK', 'MONTH', 'YEAR'

    @ColumnInfo(name = "report_date")
    public String reportDate; // 格式如 '2023-10'

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "data_hash")
    public String dataHash;

    @ColumnInfo(name = "update_time")
    public long updateTime;
}
