package com.example.expensemanager.repo

import androidx.lifecycle.LiveData
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.Currency
import com.example.expense_manager.database.Dao

class CurrencyRepository (private val currencydao: Dao){
    fun getcurrencysymbol(currencyid:Int):LiveData<List<Currency>>{
        return currencydao.getcurrencysymbol(currencyid)
    }


}