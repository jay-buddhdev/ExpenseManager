package com.example.expensemanager.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.RoomDatabase
import com.example.expense_manager.database.TransAccount
import com.example.expensemanager.repo.AccountRepository
import com.example.expensemanager.repo.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application): AndroidViewModel(application) {
    private val db: RoomDatabase = RoomDatabase.getInstance(application)
    private val repository: TransactionRepository


    init {
        val transactiondao=RoomDatabase.getInstance(application).dao()
        repository=TransactionRepository(transactiondao)
    }

    suspend fun insert(transaction: TransAccount) {
        viewModelScope.launch(Dispatchers.IO){
            repository.insert(transaction)
        }

    }
}