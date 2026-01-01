package com.example.smart_expense_tracker.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smart_expense_tracker.database.entity.CategoryEntity;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    long insert(CategoryEntity category);

    @Update
    void update(CategoryEntity category);

    @Delete
    void delete(CategoryEntity category);

    @Query("SELECT * FROM categories")
    List<CategoryEntity> getAllCategories();

    @Query("SELECT * FROM categories WHERE type = :type")
    List<CategoryEntity> getCategoriesByType(int type); // 0:支出, 1:收入

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    CategoryEntity getCategoryById(int id);
}
