package com.example.expensemanager

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.expense_manager.database.Currency
import com.example.expensemanager.adapter.CurrencyAdapter
import com.example.expensemanager.model.CurrencyViewModel
import kotlinx.android.synthetic.main.activity_select_currency.*
import kotlin.collections.ArrayList


class SelectCurrency : AppCompatActivity() {

    private lateinit var currencymodel: CurrencyViewModel

    var currencyList: ArrayList<Currency?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_currency)
        currencyList= arrayListOf()
        currencymodel = ViewModelProvider(this).get(CurrencyViewModel::class.java)

        currencymodel.allCurrency?.observe(this, Observer { currency ->
            currencyList?.addAll(currency)
            setupRecyclerView(currencyList)
        })

        searchcurrency.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do Nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s?.length!! >0) {
                    if (currencyList != null) {
                        setupRecyclerView(currencyList!!.filter { e-> e!!.CurrencyName!!.contains(s.toString(),ignoreCase = true)} as ArrayList<Currency?>)
                    }
                } else
                    setupRecyclerView(currencyList)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })


    }

    private fun setupRecyclerView(List: ArrayList<Currency?>?) {
        recycler_currency.adapter = CurrencyAdapter(List) {


            /*textView.setText(it.CurrencyName + "  -  " + it.CurrencySymbol)
            // textView.text = it.CurrencyName
            currencyId = it.CurrencyId
            textViewhint.setHint("Currency")*/
            val cid: Int? = it.CurrencyId

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("CURRENCY_NAME", it.CurrencyName)
            intent.putExtra("CURRENCY_ID", cid)
            intent.putExtra("CURRENCY_SYMBOL", it.CurrencySymbol)
            setResult(RESULT_OK, intent)
            finish()

        }


    }
}


