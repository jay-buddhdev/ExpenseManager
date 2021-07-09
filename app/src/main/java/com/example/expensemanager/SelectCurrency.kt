package com.example.expensemanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.expense_manager.database.Currency
import com.example.expensemanager.adapter.CurrencyAdapter
import com.example.expensemanager.model.CurrencyViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_select_currency.*


class SelectCurrency : AppCompatActivity() {

    private lateinit var currencymodel: CurrencyViewModel

    var currencyList: ArrayList<Currency?>? = null
    private var mInterstitialAd: InterstitialAd? = null
    lateinit var sharedPref: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_currency)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        sharedPref = getSharedPreferences(
            "Transaction",
            Context.MODE_PRIVATE
        )
        var i: Int = sharedPref.getInt("count", -1);
        if (i == 15) {
            InterstitialAdLoad()
            i=0
            i = 0
            sharedPref.edit().putInt("count", i).commit()
        } else {
            i++
            sharedPref.edit().putInt("count", i).commit()



        }
        setTitle("Select Currency")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        currencyList= arrayListOf()
        currencymodel = ViewModelProvider(this).get(CurrencyViewModel::class.java)

        currencymodel.allCurrency?.observe(this, Observer { currency ->
            currencyList?.addAll(currency)
            setupRecyclerView(currencyList)
        })

       /* img_back_currency.setOnClickListener {
            val Intent=Intent(this, MainActivity::class.java)
            startActivity(Intent)
            finish()
        }*/
        searchcurrency.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do Nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s?.length!! > 0) {
                    if (currencyList != null) {
                        setupRecyclerView(currencyList!!.filter { e ->
                            e!!.CurrencyName!!.contains(
                                s.toString(),
                                ignoreCase = true
                            )
                        } as ArrayList<Currency?>)
                    }
                } else
                    setupRecyclerView(currencyList)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        recycler_currency.setOnTouchListener(OnTouchListener { v, event ->
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            false
        })


    }

    private fun InterstitialAdLoad() {

        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-1223286865449377/3257022762",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {

                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {

                    mInterstitialAd = interstitialAd
                    if (mInterstitialAd != null) {
                        mInterstitialAd!!.show(this@SelectCurrency)
                    } else {

                    }
                }

            })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
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


