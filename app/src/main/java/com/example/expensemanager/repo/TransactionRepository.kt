package com.example.expensemanager.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.Dao
import com.example.expense_manager.database.TransAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionRepository(private val transactiondao: Dao) {



    suspend fun insert(transaction: TransAccount) {
        transactiondao.insertTransaction(transaction)
    }
    suspend fun deleteAccountTrans(accid: Int)
    {
       transactiondao.deleteAccountTrans(accid)
    }

}