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


    @Query("select * from Mst_Currency order by CurrencyName")
    fun getAllCurrency(): LiveData<List<Currency?>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransAccount)

    @Query("Update Mst_Account set Balance=:newbal,AccountModfiedDate=:date where AccountId=:accid")
    suspend fun updateAccountBalance(newbal:Double,date:String,accid:Int)

    @Query("SELECT * FROM TransAccount where AccountId=:accid order by AccountTransDate ")
    fun readTransaction(accid: Int): LiveData<List<TransAccount>>

    @Query("SELECT * FROM Mst_Account where AccountId=:accid ")
    fun readBalance(accid: Int):LiveData<Account>

    @Query("Delete From Mst_Account where AccountId=:accid")
    suspend fun deleteAccount(accid: Int)

    @Query("Delete From TransAccount where AccountId=:accid")
    suspend fun deleteAccountTrans(accid: Int)

    @Query("Update Mst_Account set AccountName=:accname,CurrencyId=:currencyid,CurrencySymbol=:currencysymbol,AccountModfiedDate=:date where AccountId=:accid")
    suspend fun updateAccount(accname:String,currencyid:Int,currencysymbol:String,date:String,accid: Int)

    @Query("Update TransAccount set Balance=Balance+:diffrence where AccountTransId >:transid and AccountId=:accid")
    suspend fun updateTrailingTransaction(diffrence:Double,transid:Int,accid:Int)

    @Query("Update TransAccount set AccountTranType=:Acctranstype,Amount=:Amount,Balance=:balance,Description=:Description,AccountTransDate=:TransDate,AccountTransModifiedDate=:TransDateModif where AccountTransId=:transid")
    suspend fun updateTranscation(
        Acctranstype:String,
        Amount: Double,
        balance: Double,
        Description:String,
        TransDate:String,
        TransDateModif:String,
        transid:Int)

    @Query("Select * From TransAccount where AccountId=:accid order by AccountTransId DESC LIMIT 1")
    fun readLastTransaction(accid:Int):LiveData<TransAccount>

    @Query("Delete From TransAccount where AccountTransId=:Transid")
     fun deleteTrans(Transid: Int)

}