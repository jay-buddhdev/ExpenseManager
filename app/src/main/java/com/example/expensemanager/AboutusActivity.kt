package com.example.expensemanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_aboutus.*


class AboutusActivity : AppCompatActivity() {

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
        } else {
            i++
            sharedPref.edit().putInt("count", i).commit()
            Toast.makeText(this,"Count "+i,Toast.LENGTH_SHORT).show()


        }



        setTitle("About us")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        aboutus_shareapp_txt.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Hey check out my app at:" + "https://tiny.cc/EXPMAN"
            )
            startActivity(Intent(intent))
        }

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
}