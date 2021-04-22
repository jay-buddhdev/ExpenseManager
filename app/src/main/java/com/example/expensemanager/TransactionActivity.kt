package com.example.expensemanager

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expense_manager.database.Account
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.android.synthetic.main.activity_transaction.*


class TransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)


        val account=getIntent().getParcelableExtra<Account>("Accountmodel")
       // val args = intent.getBundleExtra("Accountmodel")
        //val account = args!!.getSerializable("ARRAYLIST") as List<Account>?
        txtaccname.setText(account?.AccountName)
        txtbalanceview.setText(account?.CurrencySymbol+" "+account?.Balance)

        fb_add_account.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            intent.putExtra("Accountmodel",account)
            startActivity(intent)
        }


    }
    
}