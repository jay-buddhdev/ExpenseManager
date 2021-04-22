package com.example.expensemanager.repo

import androidx.lifecycle.LiveData
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.Dao
import com.example.expense_manager.database.TransAccount

class TransactionRepository(private val transactiondao: Dao) {



    suspend fun insert(transaction: TransAccount) {
        transactiondao.insertTransaction(transaction)
    }

}