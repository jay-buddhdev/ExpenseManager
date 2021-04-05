package com.example.expense_manager.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "Mst_Account")
data class Account(


    @PrimaryKey(autoGenerate = true)
    val AccountId:Int? = null,

    @NonNull
    @ColumnInfo(name = "AccountName") var AccountName: String? = null,

    @NonNull
    @ColumnInfo(name = "CurrencyId") var CurrencyId: Int? = -1,

    @NonNull
    @ColumnInfo(name = "AccountCreatedDate") var AccountCreatedDate: String? = null,

    @NonNull
    @ColumnInfo(name = "AccountModfiedDate") var AccountModfiedDate: String? = null,

    @ColumnInfo(name = "Remark") val Remark: String? = null


    )
