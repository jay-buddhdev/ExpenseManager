package com.example.expense_manager.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "TransAccount")
data class TransAccount(

        @PrimaryKey(autoGenerate = true)
        val AccountTransId:Int? = null,


        @NonNull
        @ColumnInfo(name = "AccountId")
        var AccountId:Int?=-1,

        @NonNull
        @ColumnInfo(name = "AccountTranType")
        var AccountTranType:String?=null,

        @NonNull
        @ColumnInfo(name = "Amount")
        var Amount:Double?=-1.00,

        @ColumnInfo(name = "Description")
        var Description:String?=null,

        @NonNull
        @ColumnInfo(name = "AccountTransDate")
        var AccountTransDate:String?=null,

        @NonNull
        @ColumnInfo(name = "AccountTransModifiedDate")
        var AccountTransModifiedDate:String?=null,

        @NonNull
        @ColumnInfo(name = "Balance") var Balance: Double?=-1.00,

        @ColumnInfo(name = "Remark") val Remark: String?=null


)
