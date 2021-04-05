package com.example.expense_manager.database



import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(currency: Currency)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAcccount(account: Account)

    @Query("SELECT * FROM Mst_Account ")
    fun readAllAccount(): LiveData<List<Account>>



    @Query("select * from Mst_Currency")
    fun getAllCurrency(): LiveData<List<Currency?>>?


}