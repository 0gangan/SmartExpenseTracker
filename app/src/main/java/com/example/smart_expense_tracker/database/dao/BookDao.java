package com.example.smart_expense_tracker.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smart_expense_tracker.database.entity.BookEntity;

import java.util.List;

@Dao
public interface BookDao {
    @Insert
    long insert(BookEntity book);

    @Update
    void update(BookEntity book);

    @Delete
    void delete(BookEntity book);

    @Query("SELECT * FROM books")
    List<BookEntity> getAllBooks();

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    BookEntity getBookById(int id);

    @Query("SELECT * FROM books WHERE is_default = 1 LIMIT 1")
    BookEntity getDefaultBook();

    @Query("UPDATE books SET is_default = 0")
    void resetDefaultStatus();

    default void setDefaultBook(BookEntity book) {
        resetDefaultStatus();
        book.isDefault = 1;
        update(book);
    }
}
