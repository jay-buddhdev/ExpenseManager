package com.example.expensemanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_aboutus.*


class AboutusActivity : AppCompatActivity() , View.OnClickListener{

    private var mInterstitialAd: InterstitialAd? = null
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutus)

        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)


        sharedPref = getSharedPreferences(
            "Transaction",
            Context.MODE_PRIVATE
        )
        var i: Int = sharedPref.getInt("count", -1);
        if (i == 15) {
            InterstitialAdLoad()
            i = 0
            sharedPref.edit().putInt("count", i).commit()
        } else {
            i++
            sharedPref.edit().putInt("count", i).commit()



        }



        title = "About us"
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        aboutus_shareapp_txt.setOnClickListener(this)
        aboutus_MoreApp_txt.setOnClickListener(this)
        aboutus_Rate_txt.setOnClickListener(this)
        aboutus_check_update_txt.setOnClickListener(this)


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
                        mInterstitialAd!!.show(this@AboutusActivity)
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

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.aboutus_shareapp_txt->{
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, "Hey check out My App at:" + "https://tiny.cc/EXPMAN")
                startActivity(Intent(intent))
            }
            R.id.aboutus_MoreApp_txt-> {

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, "Hey check out Other Apps at:" + "https://tiny.cc/EXPMAN")
                startActivity(Intent(intent))
            }
            R.id.aboutus_Rate_txt->
            {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, "Hey Rate Our App at:" + "https://tiny.cc/EXPMAN")
                startActivity(Intent(intent))
            }
            R.id.aboutus_check_update_txt->
            {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, "Check for Update at:" + "https://tiny.cc/EXPMAN")
                startActivity(Intent(intent))
            }


        }

    }
}