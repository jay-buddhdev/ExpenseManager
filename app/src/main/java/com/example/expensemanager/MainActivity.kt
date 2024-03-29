package com.example.expensemanager


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.Currency
import com.example.expense_manager.database.RoomDatabase
import com.example.expensemanager.adapter.AccountAdapter
import com.example.expensemanager.adapter.CurrencyAdapter
import com.example.expensemanager.model.AccountViewModel
import com.example.expensemanager.model.CurrencyViewModel
import com.example.expensemanager.model.TransactionViewModel
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.account_recyclerview.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.transaction_table_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    lateinit var mAdView: AdView

    private val currencyList = ArrayList<Currency>()
    private lateinit var db: RoomDatabase
    var currencyId: Int? = 29

    private lateinit var currencyAdapter: CurrencyAdapter
    private lateinit var accountAdapter: AccountAdapter

    private lateinit var currencymodel: CurrencyViewModel
    private lateinit var accountmodel: AccountViewModel
    private lateinit var tranactionmodel: TransactionViewModel
    var symbol: String? = "$"
    var cname: String? = "USD"
    var cid: String? = "29"

    var dialogView: View? = null
    var dialog: Dialog? = null
    var dialogview_setting: View? = null
    var dialog_setting: Dialog? = null
    private var mInterstitialAd: InterstitialAd? = null
    lateinit var sharedPref: SharedPreferences


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        tranactionmodel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        MobileAds.initialize(this) {}
        adview()
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)

        //To Account Data into Recyclerview
        fetchaccountrecyclerview()
        db = RoomDatabase.getInstance(applicationContext)


        fb_account.setOnClickListener {

            showDialog()
        }
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





        val share=Intent(Intent.ACTION_SEND)
        val button = findViewById<ImageView>(R.id.image_menu)
        image_menu.setOnClickListener {

            val popupMenu: PopupMenu = PopupMenu(this,button)
            popupMenu.menuInflater.inflate(R.menu.share_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.aboutus ->
                    {
                        val i=Intent(this,AboutusActivity::class.java)
                        startActivity(i)

                    }

                    R.id.share_app ->
                    {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_TEXT, "Hey check out my app at:" + "https://tiny.cc/EXPMAN")
                        startActivity(Intent(intent))

                    }

                       


                }
                true
            })
            popupMenu.show()

    }

        //Navigation Drawer
        /*setSupportActionBar(findViewById(R.id.toolbar))
        getSupportActionBar()?.setDisplayShowTitleEnabled(false);

       val  toggle = ActionBarDrawerToggle(
           this,
           drawer_layout,
           toolbar,
           R.string.nav_open,
           R.string.nav_close
       )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        drawer_layout.setDrawerListener(toggle)
        //toggle.setHomeAsUpIndicator(R.drawable.hamburger_icon)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.hamburger_icon)

        navigation_view.setNavigationItemSelectedListener(this)*/


    }

    private fun InterstitialAdLoad() {

        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,"ca-app-pub-1223286865449377/3257022762",adRequest,object : InterstitialAdLoadCallback()
        {
            override fun onAdFailedToLoad(adError: LoadAdError) {

                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {

                mInterstitialAd = interstitialAd
                if (mInterstitialAd != null) {
                    mInterstitialAd!!.show(this@MainActivity)
                } else {

                }
            }

        })

    }


/*
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_account -> Toast.makeText(this, "Account", Toast.LENGTH_SHORT).show()
            R.id.settings -> {
                showSettingDialog()
            }
            R.id.nav_about -> Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()

        }
        drawer_layout.closeDrawers()
        return true
    }*/

    private fun showSettingDialog() {
        val dialogview_setting = layoutInflater.inflate(R.layout.setting_dialog, null)
        dialog_setting = Dialog(this)
        dialog_setting?.setContentView(dialogview_setting!!)
        dialog_setting?.show()

        // dialogview_setting?.txtCurrencyview_sett


    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchaccountrecyclerview() {
        accountmodel = ViewModelProvider(this).get(AccountViewModel::class.java)
        accountmodel.allaccount.observe(this,
            { accounts ->
                if (accounts.isNullOrEmpty()) {
                    //showDialog()
                } else {

                    accountAdapter = AccountAdapter(accounts as ArrayList<Account>, {
                        val intent = Intent(this, TransactionActivity::class.java)

                        intent.putExtra("Accountmodel", it)
                        startActivity(intent)


                    }, {
                        //EditAccount
                        val intent = Intent(this, Update_Account_Activity::class.java)

                        intent.putExtra("Accountmodel", it)
                        startActivity(intent)
                        swipe_layout_account.close(true)

                    },
                        {
                            //Delete Account
                            it ,pos->
                            val builder = AlertDialog.Builder(this@MainActivity)
                            builder.setMessage("Are you sure want to Delete?")
                                .setCancelable(false)
                                .setPositiveButton("Yes") { dialog, id ->
                                    tranactionmodel.deleteAccountTrans(it.AccountId)
                                    accountmodel.deleteAccount(it.AccountId)
                                    accounts.remove(it)
                                    recycler_account.adapter?.notifyDataSetChanged()

                                }
                                .setNegativeButton("No") { dialog, id ->
                                    // Dismiss the dialog
                                    dialog.dismiss()
                                    accountAdapter.getViewBinder().closeLayout(it.AccountId.toString())

                                }
                            val alert = builder.create()
                            alert.show()

                        }

                    )
                    recycler_account.adapter = accountAdapter
                }
            })
    }

    override fun onResume() {
        super.onResume()
        var i: Int = sharedPref.getInt("count", -1);
        if (i == 15) {
            InterstitialAdLoad()
            i = 0
            sharedPref.edit().putInt("count", i).commit()

        } else {
            i++
            sharedPref.edit().putInt("count", i).commit()



        }

    }

    private fun adview() {


        mAdView = findViewById(R.id.adView_Main_Activity)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object: AdListener() {
            override fun onAdLoaded() {



            }

            override fun onAdFailedToLoad(adError: LoadAdError) {

            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDialog() {

        dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)
        dialog = Dialog(this)
        dialog?.setContentView(dialogView!!)
        dialog?.show()
        dialog?.setCanceledOnTouchOutside(false)

        dialogView?.edit_account_name?.requestFocus()
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(dialogView?.edit_account_name, InputMethodManager.SHOW_IMPLICIT)
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);





        dialogView?.txtCurrencyview?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                // dialogView?.txtCurrencyview?.setText(cname + " - " + symbol)

                dialogView?.currency_name_hint?.setHint("Currency")
                val i = Intent(applicationContext, SelectCurrency::class.java)
                startActivityForResult(i, 1)
                // dialog?.setView(txtCurrencyview)

            }
        }
        dialogView?.txtCurrencyview?.setOnClickListener {

            //dialogView?.txtCurrencyview?.setText(cname + " - " + symbol)

            dialogView?.currency_name_hint?.setHint("Currency")
            val i = Intent(applicationContext, SelectCurrency::class.java)
            startActivityForResult(i, 1)
            // dialog?.setView(txtCurrencyview)


            //showBottomSheet(dialogView.txtCurrencyview, dialogView.currency_name_hint)
        }

        dialogView?.btn_createaccount?.setOnClickListener {

            if (TextUtils.isEmpty(dialogView?.edit_account_name?.text)) {
                Toast.makeText(this, "Please Enter Name", Toast.LENGTH_LONG).show()
            } else {

                val date: String =
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                val account_name = dialogView?.edit_account_name?.text
                val model = Account()
                model.AccountName = account_name.toString()
                model.CurrencySymbol = symbol
                model.CurrencyId = currencyId
                model.AccountCreatedDate = date
                model.AccountModfiedDate = date
                model.Balance = 0.0

                GlobalScope.launch(Dispatchers.Main) {
                    accountmodel.insert(model)

                }
                dialogView?.txtCurrencyview?.setText("")
                dialog?.dismiss()

            }
        }

        dialogView?.btn_cancel?.setOnClickListener {
            dialogView?.txtCurrencyview?.setText("")
            dialog?.dismiss()
        }

    }

    private fun showBottomSheet(textView: EditText, textViewhint: TextInputLayout) {
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

        currencymodel = ViewModelProvider(this).get(CurrencyViewModel::class.java)
        currencymodel.allCurrency?.observe(this, { currency ->
            recycle.adapter = CurrencyAdapter(currency as ArrayList<Currency>) {
                textView.setText(it.CurrencyName + "  -  " + it.CurrencySymbol)
                // textView.text = it.CurrencyName
                currencyId = it.CurrencyId
                textViewhint.setHint("Currency")
                bottomSheetDialog.dismiss()
            }
        })


        bottomSheetDialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {

            symbol = data?.getStringExtra("CURRENCY_SYMBOL")
            cname = data?.getStringExtra("CURRENCY_NAME")
            currencyId = data?.getIntExtra("CURRENCY_ID", 29)

            dialogView?.txtCurrencyview?.setText(cname + " - " + symbol)
        }
    }


}


