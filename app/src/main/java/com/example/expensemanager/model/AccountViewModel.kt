package com.example.expensemanager.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.RoomDatabase
import com.example.expensemanager.repo.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountViewModel(application: Application) : AndroidViewModel(application) {
    private val db: RoomDatabase = RoomDatabase.getInstance(application)
    var allaccount : LiveData<List<Account>> =db.dao().readAllAccount()

    private val repository: AccountRepository

    init {
        val accountdao=RoomDatabase.getInstance(application).dao()
        repository= AccountRepository(accountdao)
        allaccount=repository.allaccount
    }


    suspend fun insert(account: Account) {
        viewModelScope.launch(Dispatchers.IO){
            repository.insert(account)
        }

    }
    suspend fun updateAccountBalance(newbal:Double,date:String,accid:Int)
    {
        viewModelScope.launch (Dispatchers.IO){
            repository.updateAccountBalance(newbal,date,accid)
        }
    }
    fun deleteAccount(accid: Int?)
    {
        viewModelScope.launch (Dispatchers.IO){
            if (accid != null) {
                repository.deleteAccount(accid)
            }
        }
    }
    suspend fun updateAccount(accname:String,currencyid:Int,currencysymbol:String,date:String,accid: Int)
    {
        viewModelScope.launch (Dispatchers.IO){
            repository.updateAccount(accname,currencyid,currencysymbol,date,accid)
        }
    }
}