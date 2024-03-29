package com.example.expensemanager.repo

import androidx.lifecycle.LiveData
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.Dao

class AccountRepository(private val accountdao: Dao) {

    val allaccount : LiveData<List<Account>> =accountdao.readAllAccount()

    suspend fun insert(account: Account) {
        accountdao.insertAcccount(account)
    }
    suspend fun updateAccountBalance(newbal: Double, date: String, accid: Int)
    {
        accountdao.updateAccountBalance(newbal,date,accid)
    }
    suspend fun deleteAccount(accid: Int)
    {
        accountdao.deleteAccount(accid)
    }
    suspend fun updateAccount(accname:String,currencyid:Int,currencysymbol:String,date:String,accid: Int)
    {
        accountdao.updateAccount(accname,currencyid,currencysymbol,date,accid)
    }



}