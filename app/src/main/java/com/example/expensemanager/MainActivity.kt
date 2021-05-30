package com.example.expensemanager


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var mAdView : AdView

    private val currencyList = ArrayList<Currency>()
    private lateinit var db: RoomDatabase
    var currencyId: Int? = 29

    private lateinit var currencyAdapter: CurrencyAdapter

    private lateinit var currencymodel: CurrencyViewModel
    private lateinit var accountmodel: AccountViewModel
    var symbol: String? = "$"
    var cname: String? = "USD"
    var cid: String? = "29"

    var dialogView: View? = null
    var dialog: Dialog? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPref: SharedPreferences = getSharedPreferences(
            "Currency_Data",
            Context.MODE_PRIVATE
        )
        if (sharedPref.getBoolean("database", true)) {
            Thread {
                populateDatabase(db)
            }.start()

            sharedPref.edit().putBoolean("database", false).commit()
        }
        MobileAds.initialize(this) {}
        adview()
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)

        //To Account Data into Recyclerview
        fetchaccountrecyclerview()
        db = RoomDatabase.getInstance(applicationContext)

        fb_account.setOnClickListener {

            showDialog()
        }
        setSupportActionBar(findViewById(R.id.toolbar))
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

        navigation_view.setNavigationItemSelectedListener(this)




    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_account -> Toast.makeText(this, "Account", Toast.LENGTH_SHORT).show()
            R.id.settings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            R.id.nav_about -> Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()

        }
        drawer_layout.closeDrawers()
        return true
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchaccountrecyclerview() {
        accountmodel = ViewModelProvider(this).get(AccountViewModel::class.java)
        accountmodel.allaccount.observe(this,
            { accounts ->
                if (accounts.isNullOrEmpty()) {
                    showDialog()
                } else {
                    recycler_account.adapter = AccountAdapter(accounts) {
                        val intent = Intent(this, TransactionActivity::class.java)
                        //val args = Bundle()
                        //args.putSerializable("ARRAYLIST", it as Serializable?)
                        intent.putExtra("Accountmodel", it)
                        startActivity(intent)

                    }
                }

            })
    }

    private fun adview() {


        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDialog() {

        dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)
        dialog= Dialog(this)
        dialog?.setContentView(dialogView!!)
       dialog?.show()

        dialogView?.edit_account_name?.requestFocus()
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(dialogView?.edit_account_name, InputMethodManager.SHOW_IMPLICIT)
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);





        dialogView?.txtCurrencyview?.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus)
            {
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
                model.CurrencySymbol=symbol
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

    private fun populateDatabase(db: RoomDatabase) {
        db.dao()

        val mCSVfile = "currency.csv"
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


