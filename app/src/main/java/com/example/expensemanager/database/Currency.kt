package com.example.expense_manager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Mst_Currency")
data class Currency(

    @PrimaryKey(autoGenerate = true)
    val CurrencyId:Int? = null,

    @ColumnInfo(name = "CurrencyName")
    var CurrencyName: String?=null,

    @ColumnInfo(name = "CurrencySymbol")
    var CurrencySymbol: String?=null
)
