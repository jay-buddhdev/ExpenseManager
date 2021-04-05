package com.example.expensemanager.model

class CurrencyModel(Currency_Name:String? = null,Currency_Symbol:String? = null) {


    var Currency_Name:String?=null
    var Currency_Symbol:String?=null

    init {
        this.Currency_Name=Currency_Name!!
        this.Currency_Symbol=Currency_Symbol!!
    }

}