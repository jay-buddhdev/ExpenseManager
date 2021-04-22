package com.example.expense_manager.database

import android.content.ContentValues
import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.expensemanager.model.CurrencyModel
import java.io.*


@Database(entities = [Account::class, Currency::class, TransAccount::class], version = 1)


abstract class RoomDatabase : androidx.room.RoomDatabase() {
    abstract fun dao(): Dao

    companion object {
        private var instance: RoomDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): RoomDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, RoomDatabase::class.java,
                    "ExpenseManager.db"
                ).fallbackToDestructiveMigration().build()

            return instance!!

        }





    }
}