package com.example.expensemanager

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expense_manager.database.Currency
import com.example.expensemanager.adapter.CurrencyAdapter
import com.example.expensemanager.model.CurrencyViewModel
import kotlinx.android.synthetic.main.activity_select_currency.*

class SelectCurrency : AppCompatActivity() {

    private lateinit var currencymodel: CurrencyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_currency)

        currencymodel= ViewModelProvider(this).get(CurrencyViewModel::class.java)
        currencymodel.allCurrency?.observe(this, Observer { currency ->
            recycler_currency.adapter = CurrencyAdapter(currency as List<Currency>) {


                /*textView.setText(it.CurrencyName + "  -  " + it.CurrencySymbol)
                // textView.text = it.CurrencyName
                currencyId = it.CurrencyId
                textViewhint.setHint("Currency")*/
                val cid:Int?=it.CurrencyId

                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("CURRENCY_NAME",it.CurrencyName)
                intent.putExtra("CURRENCY_ID",cid)
                intent.putExtra("CURRENCY_SYMBOL",it.CurrencySymbol)
                setResult(RESULT_OK,intent)
                finish()

            }
        })
    }
}