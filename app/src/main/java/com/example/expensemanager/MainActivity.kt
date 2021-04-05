package com.example.expensemanager

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils

import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider


import androidx.recyclerview.widget.RecyclerView
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.Currency
import com.example.expense_manager.database.RoomDatabase
import com.example.expensemanager.adapter.CurrencyAdapter
import com.example.expensemanager.model.AccountViewModel

import com.example.expensemanager.model.CurrencyViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.time.LocalDateTime
import java.util.*
import java.util.List.of


class MainActivity : AppCompatActivity() {

    private val currencyList = ArrayList<Currency>()
    private lateinit var db:RoomDatabase
    var currencyId:Int?=null

    private lateinit var currencyAdapter: CurrencyAdapter

    private lateinit var currencymodel: CurrencyViewModel
    private lateinit var accountmodel: AccountViewModel



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPref: SharedPreferences = getSharedPreferences(
            "Currency_Data",
            Context.MODE_PRIVATE
        )


        accountmodel=ViewModelProvider(this).get(AccountViewModel::class.java)
        /*val db = Room.databaseBuilder(
            applicationContext,
            RoomDatabase::class.java, "ExpenseManager.db"
        )*/
        db=RoomDatabase.getInstance(applicationContext)


        if(sharedPref.getBoolean("database",true))
        {
            Thread{
                populateDatabase(db)
            }.start()

            sharedPref.edit().putBoolean("database",false).commit()
        }
        else
      {
          Toast.makeText(this, "Out", Toast.LENGTH_LONG).show()
      }
        fb_account.setOnClickListener{

            val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)

            val dialog = AlertDialog.Builder(this).setView(dialogView)
            dialog.show()

            dialogView.txtCurrencyview.setOnClickListener {
                showBottomSheet(dialogView.txtCurrency)
            }

            dialogView.btn_createaccount.setOnClickListener {

                if(TextUtils.isEmpty(dialogView.edit_account_name.text))
                {
                    Toast.makeText(this,"Please Enter Name",Toast.LENGTH_LONG).show()
                }
                else
                {
                    val account_name=dialogView.edit_account_name.text
                    val model=Account()
                    model.AccountName= account_name.toString()
                    model.CurrencyId=currencyId
                    model.AccountCreatedDate=LocalDateTime.now().toString()
                    model.AccountModfiedDate=LocalDateTime.now().toString()

                    GlobalScope.launch(Dispatchers.Main) {
                        accountmodel.insert(model)

                    }
                    Toast.makeText(this,"Done",Toast.LENGTH_SHORT).show()



                }
            }

        }



    }

    /*private fun addcurrency() {
        val curren= CurrencyModel("USD", "$")
        currencyList.add(curren)

    }

*/





    private fun showBottomSheet(textView: TextView){
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_dailog, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setContentView(dialogView)

        val recycle = dialogView.findViewById<RecyclerView>(R.id.recycler_currency)
       // addcurrency()
       /* currencyAdapter= CurrencyAdapter(currencyList){
            textView.text = it
            bottomSheetDialog.dismiss()
        }
        recycle.adapter=currencyAdapter*/

        currencymodel= ViewModelProvider(this).get(CurrencyViewModel::class.java)
        currencymodel.allCurrency?.observe(this, Observer {
                currency->
            recycle.adapter=CurrencyAdapter(currency as List<Currency>){
                textView.text = it.CurrencyName
                currencyId=it.CurrencyId
                bottomSheetDialog.dismiss()
            }
        })


        bottomSheetDialog.show()
    }
    private fun populateDatabase(db: RoomDatabase) {
        val currencyDao = db.dao()

        val mCSVfile = "currency.csv"
        val manager: AssetManager = applicationContext.getAssets()
        var inStream: InputStream? =null
        try {
            inStream = manager.open(mCSVfile)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val buffer = BufferedReader(InputStreamReader(inStream))

        var line = ""



        try {

            db.beginTransaction()
            loop@while (!buffer.readLine().also { line = it }.isNullOrEmpty()) {
                val colums = line.split(",".toRegex()).toTypedArray()

                val model = Currency()
                model.CurrencyName=colums[0].trim()
                model.CurrencySymbol=colums[1].trim()
                /*model.Currency_Name =
                model.Currency_Symbol = colums[1].trim()*/

                db.dao().insert(model)
               // Toast.makeText(this,"Done",Toast.LENGTH_SHORT).show()
                when(colums[0].trim())
                {
                    "VND" -> {
                        break@loop
                    }
                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        db.setTransactionSuccessful()
        db.endTransaction()
    }


}