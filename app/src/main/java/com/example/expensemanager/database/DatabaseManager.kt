package com.example.expense_manager.database

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.lifecycle.LiveData


class DatabaseManager(roomDatabase: RoomDatabase) : Database
{

    private val dao = roomDatabase.dao()
    private val allCurrency: LiveData<List<Currency>>? = null


     fun insert(currency: Currency) {
        dao.insert(currency)
    }
    suspend fun insert(account: Account) {
        dao.insertAcccount(account)
    }

    override fun getAllCurrency(): LiveData<List<Currency>>? {
      return allCurrency
    }

}