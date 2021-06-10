package com.example.expense_manager.database

import android.os.Parcel
import android.os.Parcelable
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


): Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readValue(Double::class.java.classLoader) as? Double,
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readValue(Double::class.java.classLoader) as? Double,
                parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeValue(AccountTransId)
                parcel.writeValue(AccountId)
                parcel.writeString(AccountTranType)
                parcel.writeValue(Amount)
                parcel.writeString(Description)
                parcel.writeString(AccountTransDate)
                parcel.writeString(AccountTransModifiedDate)
                parcel.writeValue(Balance)
                parcel.writeString(Remark)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<TransAccount> {
                override fun createFromParcel(parcel: Parcel): TransAccount {
                        return TransAccount(parcel)
                }

                override fun newArray(size: Int): Array<TransAccount?> {
                        return arrayOfNulls(size)
                }
        }
}
