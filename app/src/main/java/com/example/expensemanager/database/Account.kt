package com.example.expense_manager.database

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Mst_Account")
data class Account(


    @PrimaryKey(autoGenerate = true)
    val AccountId:Int? = null,

    @NonNull
    @ColumnInfo(name = "AccountName") var AccountName: String? = null,

    @NonNull
    @ColumnInfo(name = "CurrencyId") var CurrencyId: Int? = -1,

    @NonNull
    @ColumnInfo(name = "CurrencySymbol")
    var CurrencySymbol: String?=null,

    @NonNull
    @ColumnInfo(name = "AccountCreatedDate") var AccountCreatedDate: String? = null,

    @NonNull
    @ColumnInfo(name = "AccountModfiedDate") var AccountModfiedDate: String? = null,

    @NonNull
    @ColumnInfo(name = "Balance") var Balance: Double? =-1.00,

    @ColumnInfo(name = "Remark") val Remark: String? = null


    ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(AccountId)
        parcel.writeString(AccountName)
        parcel.writeValue(CurrencyId)
        parcel.writeString(CurrencySymbol)
        parcel.writeString(AccountCreatedDate)
        parcel.writeString(AccountModfiedDate)
        parcel.writeValue(Balance)
        parcel.writeString(Remark)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Account> {
        override fun createFromParcel(parcel: Parcel): Account {
            return Account(parcel)
        }

        override fun newArray(size: Int): Array<Account?> {
            return arrayOfNulls(size)
        }
    }
}
