package com.example.expense_manager.database



import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(currency: Currency)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAcccount(account: Account)

    @Query("SELECT * FROM Mst_Account ")
    fun readAllAccount(): LiveData<List<Account>>

    @Query("SELECT * FROM Mst_Currency where CurrencyId=:currencyid ")
    fun getcurrencysymbol(currencyid:Int):LiveData<List<Currency>>


    @Query("select * from Mst_Currency")
    fun getAllCurrency(): LiveData<List<Currency?>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransAccount)

    @Query("Update Mst_Account set Balance=:newbal where AccountId=:accid")
    suspend fun updateAccountBalance(newbal:Double,accid:Int)

    @Query("SELECT * FROM TransAccount where AccountId=:accid ")
    fun readTransaction(accid: Int): LiveData<List<TransAccount>>

}