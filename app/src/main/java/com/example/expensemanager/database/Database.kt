package com.example.expense_manager.database

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.lifecycle.LiveData


interface Database {


  public fun getAllCurrency(): LiveData<List<Currency>>?




}