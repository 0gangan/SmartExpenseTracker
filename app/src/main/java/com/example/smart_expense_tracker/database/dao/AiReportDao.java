package com.example.smart_expense_tracker.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smart_expense_tracker.database.entity.AiReportEntity;

import java.util.List;

@Dao
public interface AiReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertOrUpdate(AiReportEntity report);

    @Update
    void update(AiReportEntity report);

    @Delete
    void delete(AiReportEntity report);

    @Query("SELECT * FROM ai_reports WHERE report_type = :type AND report_date = :date LIMIT 1")
    AiReportEntity getReport(String type, String date);

    @Query("SELECT * FROM ai_reports ORDER BY update_time DESC")
    List<AiReportEntity> getAllReports();
}
