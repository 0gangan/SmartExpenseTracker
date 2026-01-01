package com.example.smart_expense_tracker.database.dao;

import androidx.room.*;
import com.example.smart_expense_tracker.database.entity.BudgetEntity;
import kotlinx.coroutines.flow.Flow;

@Dao
public interface BudgetDao {
    
    @Query("SELECT * FROM budget ORDER BY year DESC, month DESC, category")
    Flow<BudgetEntity> getAllBudgets();
    
    @Query("SELECT * FROM budget WHERE year = :year AND month = :month ORDER BY category")
    Flow<BudgetEntity> getBudgetsByMonth(int year, int month);
    
    @Query("SELECT * FROM budget WHERE id = :id")
    BudgetEntity getBudgetById(int id);
    
    @Query("SELECT * FROM budget WHERE category = :category AND year = :year AND month = :month")
    BudgetEntity getBudgetByCategoryAndMonth(String category, int year, int month);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BudgetEntity budget);
    
    @Update
    void update(BudgetEntity budget);
    
    @Delete
    void delete(BudgetEntity budget);
    
    @Query("DELETE FROM budget WHERE id = :id")
    void deleteById(int id);
    
    @Query("SELECT SUM(budgetAmount) FROM budget WHERE year = :year AND month = :month")
    long getTotalBudgetByMonth(int year, int month);
    
    @Query("SELECT SUM(spentAmount) FROM budget WHERE year = :year AND month = :month")
    long getTotalSpentByMonth(int year, int month);
    
    @Query("SELECT SUM(budgetAmount - spentAmount) FROM budget WHERE year = :year AND month = :month")
    long getRemainingBudgetByMonth(int year, int month);

    @Query("DELETE FROM budget WHERE category = :category AND year = :year AND month = :month")
    void deleteByCategoryAndMonth(String category, int year, int month);
}