package com.example.expensemanager.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.expense_manager.database.Currency
import com.example.expense_manager.database.DatabaseManager
import com.example.expense_manager.database.RoomDatabase


class CurrencyViewModel(application: Application) : AndroidViewModel(application) {
    private val db:RoomDatabase = RoomDatabase.getInstance(application)
    internal val allCurrency : LiveData<List<Currency?>>? = db.dao().getAllCurrency()


    var currency : MutableLiveData<Currency>? = null

}