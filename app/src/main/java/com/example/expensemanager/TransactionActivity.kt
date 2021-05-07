package com.example.expensemanager

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.RoomDatabase
import com.example.expense_manager.database.TransAccount
import com.example.expensemanager.adapter.TransacationAdapter
import com.example.expensemanager.model.AccountViewModel
import com.example.expensemanager.model.TransactionViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_transaction.*
import kotlinx.android.synthetic.main.add_transcation_custom_dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class TransactionActivity : AppCompatActivity() {

    private lateinit var db: RoomDatabase
    var dialogView: View? = null
    var dialog: Dialog? = null
    private lateinit var transactionmodel: TransactionViewModel
    private lateinit var accountmodel: AccountViewModel
    var AccountList: ArrayList<Account?>? = null

    private var account : Account? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)
        account = intent.getParcelableExtra<Account>("Accountmodel")
        db = RoomDatabase.getInstance(applicationContext)
        transactionmodel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        accountmodel = ViewModelProvider(this).get(AccountViewModel::class.java)
        AccountList = arrayListOf()


        // val args = intent.getBundleExtra("Accountmodel")
        //val account = args!!.getSerializable("ARRAYLIST") as List<Account>?


        fb_add_account.setOnClickListener {

            /*val intent = Intent(this, AddTransactionActivity::class.java)
            intent.putExtra("Accountmodel",account)
            finish()
            startActivity(intent)*/
            showDialog()


        }
        txtaccname.setText(account?.AccountName)
        txtbalanceview.setText(account?.CurrencySymbol + " " + account?.Balance)

        //fetchTransaction()
        val accid: Int? = account?.AccountId
        if (accid != null) {
            db.dao().readTransaction(accid).observe(this) { Transactions ->
                recycle_table.adapter = TransacationAdapter(Transactions)
            }
        }


    }

    private fun showDialog() {
        dialogView = layoutInflater.inflate(R.layout.add_transcation_custom_dialog, null)
        dialog = Dialog(this)
        dialog?.setContentView(dialogView!!)
        dialog?.show()


        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = sdf.format(Date())
        dialog?.datepick?.setText(currentDate)
        val materialDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .build()

        dialog?.datepick?.setOnClickListener {
            materialDatePicker.show(getSupportFragmentManager(), "Datepickerdialog")
            materialDatePicker.addOnPositiveButtonClickListener { selection -> // Get the offset from our timezone and UTC.
                val timeZoneUTC = TimeZone.getDefault()
                // It will be negative, so that's the -1
                val offsetFromUTC = timeZoneUTC.getOffset(Date().time) * -1
                // Create a date format, then a date object with our offset
                val simpleFormat = SimpleDateFormat("dd/MM/yyyy")
                val date = Date(selection + offsetFromUTC)
                dialog?.datepick?.setText(simpleFormat.format(date))
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

            //  Toast.makeText(this,AccountList?.get(0)?.Balance.toString(),Toast.LENGTH_SHORT).show()

            val amount: Int = Integer.parseInt(dialog?.edit_amount?.text.toString())
            val newbal = account?.Balance?.plus(amount)
            model.Balance = newbal
            model.AccountTranType = "CR"
            GlobalScope.launch(Dispatchers.Main) {
                transactionmodel.insert(model)
                if (newbal != null) {

                    accountmodel.updateAccountBalance(newbal, account?.AccountId!!)
                    db.dao().readBalance(account?.AccountId!!).observe(this@TransactionActivity) { acc ->
                        account = acc
                    }

                }


            }

            Toast.makeText(this, "Transcation Completed", Toast.LENGTH_SHORT).show()
            txtbalanceview.setText(account?.CurrencySymbol + " " + newbal)
            dialog?.dismiss()

        } else {

            val amount: Int = Integer.parseInt(dialog?.edit_amount?.text?.toString())
            val newbal = account?.Balance?.minus(amount)
            model.Balance = newbal
            model.AccountTranType = "DR"
            GlobalScope.launch(Dispatchers.Main) {
                transactionmodel.insert(model)
                if (newbal != null) {
                    accountmodel.updateAccountBalance(newbal, account?.AccountId!!)
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