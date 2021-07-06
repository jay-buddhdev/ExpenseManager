package com.example.expensemanager.repo

import com.example.expense_manager.database.Dao
import com.example.expense_manager.database.TransAccount

class TransactionRepository(private val transactiondao: Dao) {



    suspend fun insert(transaction: TransAccount) {
        transactiondao.insertTransaction(transaction)
    }
    suspend fun deleteAccountTrans(accid: Int)
    {
       transactiondao.deleteAccountTrans(accid)
    }

    suspend fun updateTrailingTransaction(diffrence: Double, transid:Int, accid:Int)
    {
        transactiondao.updateTrailingTransaction(diffrence,transid,accid)
    }
    suspend fun updateTranscation(Acctranstype:String,Amount:Double,balance:Double,Description:String,TransDate:String,TransDateModif:String,transid:Int)
    {
        transactiondao.updateTranscation(Acctranstype, Amount, balance,Description, TransDate, TransDateModif, transid)
    }
    fun deleteTrans(Transid: Int)
    {
        transactiondao.deleteTrans(Transid)
    }
    fun readBalanceupto(accid: Int,newDate:String)
    {
        transactiondao.readBalanceupto(accid,newDate)
    }

}