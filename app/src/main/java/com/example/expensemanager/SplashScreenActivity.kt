package com.example.expensemanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.expense_manager.database.Currency
import com.example.expense_manager.database.RoomDatabase
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_splash_screen.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var db: RoomDatabase
    private var mInterstitialAd: InterstitialAd? = null
    lateinit var sharedPref_t: SharedPreferences


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        db = RoomDatabase.getInstance(applicationContext)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)


        sharedPref_t = getSharedPreferences(
            "Transaction",
            Context.MODE_PRIVATE
        )
        val sharedPref: SharedPreferences = getSharedPreferences(
            "Currency_Data",
            Context.MODE_PRIVATE
        )
        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.

        var i:Int=sharedPref_t.getInt("count",-1);
        if(i==15)
        {
            InterstitialAdLoad()
            i = 0
            sharedPref.edit().putInt("count", i).commit()
        }
        else
        {
            i+=i
            sharedPref_t.edit().putInt("count",i).commit()



        }

       Handler().postDelayed({

           if (sharedPref.getBoolean("database", true)) {
               Thread {
                   populateDatabase(db)
               }.start()

               sharedPref.edit().putBoolean("database", false).commit()
           }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
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
                        mInterstitialAd!!.show(this@SplashScreenActivity)
                    } else {

                    }
                }

            })

    }

    private fun populateDatabase(db: RoomDatabase) {

        db.dao()


        val mCSVfile = "currency1.csv"
        val manager: AssetManager = applicationContext.getAssets()
        var inStream: InputStream? = null
        try {
            inStream = manager.open(mCSVfile)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val buffer = BufferedReader(InputStreamReader(inStream))

        var line = ""



        try {


            loop@ while (!buffer.readLine().also { line = it }.isNullOrEmpty()) {
                val colums = line.split(",".toRegex()).toTypedArray()

                val model = Currency()
                model.CurrencyName = colums[0].trim()
                model.CurrencySymbol = colums[1].trim()
                /*model.Currency_Name =
                model.Currency_Symbol = colums[1].trim()*/

                db.dao().insert(model)
                // Toast.makeText(this,"Done",Toast.LENGTH_SHORT).show()
                when (colums[0].trim()) {
                    "VND" -> {
                        break@loop
                    }
                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }


    }
}