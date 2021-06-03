package com.example.expensemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.RoomDatabase
import com.example.expensemanager.model.AccountViewModel
import kotlinx.android.synthetic.main.activity_update__account_.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class Update_Account_Activity : AppCompatActivity() {

    private var account : Account? = null
    private lateinit var db: RoomDatabase
    var currencyId: Int? = null
    var symbol: String? = null
    var cname: String? = null
    private lateinit var accountmodel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update__account_)
        db = RoomDatabase.getInstance(applicationContext)
        accountmodel = ViewModelProvider(this).get(AccountViewModel::class.java)
        account = intent.getParcelableExtra<Account>("Accountmodel")

        loaddata(account)


        txtUpCurrencyview?.setOnFocusChangeListener{ view: View,hasFocus ->
            if(hasFocus)
            {
                val i = Intent(applicationContext, SelectCurrency::class.java)
                startActivityForResult(i, 1)
            }

        }

        txtUpCurrencyview?.setOnClickListener {
            val i = Intent(applicationContext, SelectCurrency::class.java)
            startActivityForResult(i, 1)

        }
        btn_update_account.setOnClickListener {
            updateData(account)
            Toast.makeText(this,"Account Updated",Toast.LENGTH_SHORT).show()
            val i = Intent(applicationContext, MainActivity::class.java)
            startActivity(i)
            finish()
        }
        up_btn_cancel.setOnClickListener {
            val i = Intent(applicationContext, MainActivity::class.java)
            startActivity(i)
            finish()
        }

    }

    private fun updateData(account: Account?) {
        val date: String =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        GlobalScope.launch(Dispatchers.Main) {

          accountmodel.updateAccount( edit_upaccount_name?.getText().toString(),
              currencyId!!,
              symbol!!,
              date,
              account?.AccountId!!
              )


        }



    }

    private fun loaddata(account: Account?) {

        edit_upaccount_name?.setText(account?.AccountName)
        txtupaccount?.setText(account?.AccountName)
        db.dao().getcurrencysymbol(account?.CurrencyId!!).observe(this,
            {
                txtUpCurrencyview?.setText(it[0].CurrencyName+"-"+it[0].CurrencySymbol)
                currencyId=it[0].CurrencyId
                symbol=it[0].CurrencySymbol
                cname=it[0].CurrencyName

            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == 1 && resultCode == RESULT_OK)
            {
                symbol = data?.getStringExtra("CURRENCY_SYMBOL")
                cname = data?.getStringExtra("CURRENCY_NAME")
                currencyId = data?.getIntExtra("CURRENCY_ID", 29)
                txtUpCurrencyview?.setText(cname+"-"+symbol)

            }

        }


}