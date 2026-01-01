package com.example.smart_expense_tracker.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smart_expense_tracker.database.entity.AccountEntity;

import java.util.List;

@Dao
public interface AccountDao {
    @Insert
    long insert(AccountEntity account);

    @Update
    void update(AccountEntity account);

    @Delete
    void delete(AccountEntity account);

    @Query("SELECT * FROM accounts")
    List<AccountEntity> getAllAccounts();

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    AccountEntity getAccountById(int id);

    @Query("UPDATE accounts SET balance = balance + :changeAmount WHERE id = :accountId")
    void updateBalance(int accountId, long changeAmount);

    @Query("UPDATE accounts SET color = :color WHERE id = :accountId")
    void updateColor(int accountId, int color);

    @Query("SELECT color FROM accounts WHERE id = :accountId LIMIT 1")
    int getAccountColor(int accountId);
}
