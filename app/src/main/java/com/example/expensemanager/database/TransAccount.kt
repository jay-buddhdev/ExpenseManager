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
        val AccountId:Int,

        @NonNull
        @ColumnInfo(name = "AccountTranType")
        val AccountTranType:String,

        @NonNull
        @ColumnInfo(name = "Amount")
        val Amount:Double,

        @ColumnInfo(name = "Description")
        val Description:String?,

        @NonNull
        @ColumnInfo(name = "AccountTransDate")
        val AccountTransDate:String,

        @NonNull
        @ColumnInfo(name = "AccountTransModifiedDate")
        val AccountTransModifiedDate:String,

        @ColumnInfo(name = "Remark") val Remark: String?


)
