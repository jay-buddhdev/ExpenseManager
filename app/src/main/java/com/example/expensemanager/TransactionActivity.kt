package com.example.expensemanager

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.RoomDatabase
import com.example.expense_manager.database.TransAccount
import com.example.expensemanager.adapter.TransacationAdapter
import com.example.expensemanager.model.AccountViewModel
import com.example.expensemanager.model.TransactionViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_transaction.*
import kotlinx.android.synthetic.main.add_transcation_custom_dialog.*
import kotlinx.android.synthetic.main.transaction_table_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class TransactionActivity : AppCompatActivity() {
    lateinit var mAdView : AdView
    private lateinit var db: RoomDatabase
    var dialogView: View? = null
    var dialog: Dialog? = null
    private lateinit var transactionmodel: TransactionViewModel
    private lateinit var accountmodel: AccountViewModel
    var AccountList: ArrayList<Account?>? = null

    private var account : Account? = null
    lateinit var sharedPref: SharedPreferences
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)

        MobileAds.initialize(this) {}
        adview()
        account = intent.getParcelableExtra<Account>("Accountmodel")
        db = RoomDatabase.getInstance(applicationContext)
        transactionmodel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        accountmodel = ViewModelProvider(this).get(AccountViewModel::class.java)
        AccountList = arrayListOf()

        /*img_back_transaction.setOnClickListener {
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }*/
        setTitle(account?.AccountName)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);


        // val args = intent.getBundleExtra("Accountmodel")
        //val account = args!!.getSerializable("ARRAYLIST") as List<Account>?


        fb_add_account.setOnClickListener {

            /*val intent = Intent(this, AddTransactionActivity::class.java)
            intent.putExtra("Accountmodel",account)
            finish()
            startActivity(intent)*/
            showDialog()



        }
        //txtaccname.setText(account?.AccountName)
        if(account?.Balance!!<0)
        {
            val bal= account?.Balance!!.roundToInt().toString().drop(1)
            txtbalanceview.setText(account?.CurrencySymbol + " " + bal + " " + "DR")
        }
        else
        {
            val bal=Integer.parseInt(account?.Balance!!.roundToInt().toString())
            txtbalanceview.setText(account?.CurrencySymbol + " " + bal + " " + "CR")
        }



        //fetchTransaction()
        val accid: Int? = account?.AccountId
        if (accid != null) {
            db.dao().readTransaction(accid).observe(this) { Transactions ->
                recycle_table.adapter = TransacationAdapter(Transactions as ArrayList<TransAccount>,
                    {
                        //Edit
                        val intent = Intent(this, Update_Transaction_Activity::class.java)
                        intent.putExtra("Transactionmodel", it)
                        startActivity(intent)
                    },
                    {
                        //Delete
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage("Are you sure you want to Delete?")
                            .setCancelable(false)
                            .setPositiveButton("Yes") { dialog, id ->
                                val date: String =
                                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                                var difference: Double = it?.Amount!!
                                if (it?.AccountTranType?.equals("CR") == true) {
                                    difference *= -1
                                }
                                transactionmodel.deleteTrans(it?.AccountTransId!!)
                                transactionmodel.updateTrailingTransaction(
                                    difference,
                                    it?.AccountTransId!!,
                                    it?.AccountId!!
                                )

                                db.dao().readLastTransaction(it?.AccountId!!).observe(this, {

                                    GlobalScope.launch(Dispatchers.Main) {
                                        if(it==null)
                                        {
                                            db.dao().updateAccountBalance(0.0, date,accid)
                                        }
                                        else
                                        {
                                            db.dao().updateAccountBalance(it.Balance!!, date, it?.AccountId!!)
                                        }

                                        Transactions.remove(it)
                                        recycle_table.adapter?.notifyDataSetChanged()

                                    }

                                })


                            }
                            .setNegativeButton("No") { dialog, id ->
                                // Dismiss the dialog
                                dialog.dismiss()
                                swipe_layout.close(true)
                            }
                        val alert = builder.create()
                        alert.show()


                    })
            }
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)

    }

    @SuppressLint("MissingPermission")
    private fun adview() {
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()

        mAdView.loadAd(adRequest)

    }

    private fun showDialog() {
        dialogView = layoutInflater.inflate(R.layout.add_transcation_custom_dialog, null)
        dialog = Dialog(this)
        dialog?.setContentView(dialogView!!)
        dialog?.show()
        sharedPref = getSharedPreferences(
            "Transaction",
            Context.MODE_PRIVATE
        )
        if(sharedPref.getString("type", "")?.equals("") == true)
        {
            dialog?.Rbcr?.setChecked(true)
        }
        else if(sharedPref.getString("type", "")?.equals("DR") == true)
        {
            dialog?.Rbdr?.setChecked(true)
        }
        else
        {
            dialog?.Rbcr?.setChecked(true)
        }


        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = sdf.format(Date())
        dialog?.edit_date?.setText(currentDate)
        val materialDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .build()

        dialog?.edit_date?.setOnClickListener {
            materialDatePicker.show(getSupportFragmentManager(), "Datepickerdialog")
            materialDatePicker.addOnPositiveButtonClickListener { selection -> // Get the offset from our timezone and UTC.
                val timeZoneUTC = TimeZone.getDefault()
                // It will be negative, so that's the -1
                val offsetFromUTC = timeZoneUTC.getOffset(Date().time) * -1
                // Create a date format, then a date object with our offset
                val simpleFormat = SimpleDateFormat("dd/MM/yyyy")
                val date = Date(selection + offsetFromUTC)
                dialog?.edit_date?.setText(simpleFormat.format(date))
            }
        }
        dialog?.edit_date?.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus)
            {
                materialDatePicker.show(getSupportFragmentManager(), "Datepickerdialog")
                materialDatePicker.addOnPositiveButtonClickListener { selection -> // Get the offset from our timezone and UTC.
                    val timeZoneUTC = TimeZone.getDefault()
                    // It will be negative, so that's the -1
                    val offsetFromUTC = timeZoneUTC.getOffset(Date().time) * -1
                    // Create a date format, then a date object with our offset
                    val simpleFormat = SimpleDateFormat("dd/MM/yyyy")
                    val date = Date(selection + offsetFromUTC)
                    dialog?.edit_date?.setText(simpleFormat.format(date))
                }
            }
        }

        dialog?.btn_cancel?.setOnClickListener {
            dialog?.dismiss()
        }

        dialog?.btn_ok?.setOnClickListener {

            if (TextUtils.isEmpty(dialog?.edit_amount?.text)) {
                Toast.makeText(this, "Enter Amount", Toast.LENGTH_SHORT).show()
            } else {
//                db.dao().readBalance(account?.AccountId!!).observe(this) { acc ->
//
//                    bal = acc.Balance
//
//                }
                setData()

            }
        }
    }

    private fun setData( ) {
        val date: String =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val model = TransAccount()
        model.AccountId = account?.AccountId!!
        model.AccountTransDate = date
        model.AccountTransModifiedDate = date
        model.Amount = dialog?.edit_amount?.text?.toString()!!.toDouble()
        model.Description = dialog?.edit_desc?.text?.toString()


        if (dialog?.Rbcr?.isChecked!!) {
            sharedPref.edit().putString("type", "CR").commit()
            //  Toast.makeText(this,AccountList?.get(0)?.Balance.toString(),Toast.LENGTH_SHORT).show()

            val amount: Int = Integer.parseInt(dialog?.edit_amount?.text.toString())
            val newbal = account?.Balance?.plus(amount)
            model.Balance = newbal
            model.AccountTranType = "CR"
            GlobalScope.launch(Dispatchers.Main) {
                transactionmodel.insert(model)
                if (newbal != null) {

                    accountmodel.updateAccountBalance(newbal, date.toString(), account?.AccountId!!)
                    db.dao().readBalance(account?.AccountId!!).observe(this@TransactionActivity) { acc ->
                        account = acc
                    }

                }


            }

            Toast.makeText(this, "Transcation Completed", Toast.LENGTH_SHORT).show()
            txtbalanceview.setText(account?.CurrencySymbol + " " + newbal)
            dialog?.dismiss()

        } else {
            sharedPref.edit().putString("type", "DR").commit()

            val amount: Int = Integer.parseInt(dialog?.edit_amount?.text?.toString())
            val newbal = account?.Balance?.minus(amount)
            model.Balance = newbal
            model.AccountTranType = "DR"
            GlobalScope.launch(Dispatchers.Main) {
                transactionmodel.insert(model)
                if (newbal != null) {
                    accountmodel.updateAccountBalance(newbal, date.toString(), account?.AccountId!!)
                    db.dao().readBalance(account?.AccountId!!).observe(this@TransactionActivity) { acc ->
                        account = acc
                    }
                }

            }
            Toast.makeText(this, "Transcation Completed", Toast.LENGTH_SHORT).show()
            txtbalanceview.setText(account?.CurrencySymbol + " " + newbal)
            dialog?.dismiss()
        }
    }


}