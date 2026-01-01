package com.example.smart_expense_tracker.repository

import com.example.smart_expense_tracker.database.dao.AccountDao
import com.example.smart_expense_tracker.database.dao.BookDao
import com.example.smart_expense_tracker.database.dao.CategoryDao
import com.example.smart_expense_tracker.database.dao.TransactionDao
import com.example.smart_expense_tracker.database.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao,
    private val bookDao: BookDao
) {

    fun getTransactions(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    suspend fun addTransaction(transaction: TransactionEntity) {
        transactionDao.insert(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.delete(transaction)
    }
}