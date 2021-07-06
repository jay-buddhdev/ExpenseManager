package com.example.expensemanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.Converters
import com.example.expense_manager.database.RoomDatabase
import com.example.expense_manager.database.TransAccount
import com.example.expensemanager.model.AccountViewModel
import com.example.expensemanager.model.TransactionViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_transaction.*
import kotlinx.android.synthetic.main.activity_update__transaction_.*
import kotlinx.android.synthetic.main.activity_update__transaction_.Rbcr
import kotlinx.android.synthetic.main.activity_update__transaction_.Rbdr
import kotlinx.android.synthetic.main.activity_update__transaction_.btn_cancel
import kotlinx.android.synthetic.main.add_transcation_custom_dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class Update_Transaction_Activity : AppCompatActivity() {

    private var Transaction :TransAccount? = null
    private lateinit var tranactionmodel: TransactionViewModel
    private lateinit var accountmodel: AccountViewModel
    private lateinit var db: RoomDatabase
    var account : Account? = null
    private var accountmodeldata : Account? = null
    var date:Date?=null
    var editamount:String?=null
    lateinit var sharedPref: SharedPreferences
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update__transaction_)

        Transaction= intent.getParcelableExtra<TransAccount>("Transactionmodel")
        accountmodeldata=intent.getParcelableExtra<Account>("AccountModel")
        tranactionmodel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        accountmodel = ViewModelProvider(this).get(AccountViewModel::class.java)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        sharedPref = getSharedPreferences(
            "Transaction",
            Context.MODE_PRIVATE
        )
        setTitle(Transaction?.Description)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        db = RoomDatabase.getInstance(applicationContext)
        tranactionmodel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        loaddata(Transaction)
        btn_update_transaction.setOnClickListener {
            updatetransaction()
        }
        btn_cancel.setOnClickListener {

            db.dao().readBalance(Transaction?.AccountId!!).observe(this){
                val intent= Intent(this,TransactionActivity::class.java)
                intent.putExtra("Accountmodel", it)
                startActivity(intent)
                finish()
            }
        }

        val materialDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .build()

        update_trans_date?.setOnClickListener {
            materialDatePicker.show(getSupportFragmentManager(), "Datepickerdialog")
            materialDatePicker.addOnPositiveButtonClickListener { selection -> // Get the offset from our timezone and UTC.
                val timeZoneUTC = TimeZone.getDefault()
                // It will be negative, so that's the -1
                val offsetFromUTC = timeZoneUTC.getOffset(Date().time) * -1
                // Create a date format, then a date object with our offset
                val simpleFormat = SimpleDateFormat("dd/MM/yyyy")
                date = Date(selection + offsetFromUTC)
                update_trans_date?.setText(simpleFormat.format(date))
            }
        }

        update_trans_date?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                materialDatePicker.show(getSupportFragmentManager(), "Datepickerdialog")
                materialDatePicker.addOnPositiveButtonClickListener { selection -> // Get the offset from our timezone and UTC.
                    val timeZoneUTC = TimeZone.getDefault()
                    // It will be negative, so that's the -1
                    val offsetFromUTC = timeZoneUTC.getOffset(Date().time) * -1
                    // Create a date format, then a date object with our offset
                    val simpleFormat = SimpleDateFormat("dd/MM/yyyy")
                    date = Date(selection + offsetFromUTC)
                    update_trans_date?.setText(simpleFormat.format(date))
                }

            }
        }

    }

    private fun updatetransaction() {
        val currentdate: String =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val update: Double = (Transaction?.Amount?.minus(update_trans_amount.getText().toString()?.toDouble()))!!
        val Amount: Double= update_trans_amount.getText().toString().toDouble()
        val type: String = if(Rbcr?.isChecked() == true) "CR" else "DR";
        val categoryChanged: Boolean = !(Transaction?.AccountTranType?.equals(type)!!)
        val DateChanged:Boolean=!(Transaction?.AccountTransDate?.equals(Converters.DMYtoYMD(update_trans_date?.text.toString())))!!
        if(true)
        {
            val date: String =
                SimpleDateFormat(
                    "dd-MM-yyyy",
                    Locale.getDefault()
                ).format(Date())
            var difference: Double = Transaction?.Amount!!
            if (Transaction?.AccountTranType?.equals("CR") == true) {
                difference *= -1
            }

            tranactionmodel.deleteTrans(Transaction?.AccountTransId!!)
            db.dao().updateTrailingTransaction_date(difference,Transaction?.AccountTransDate!!,Transaction?.AccountTransId!!,Transaction?.AccountId!!)
            /*tranactionmodel.updateTrailingTransaction(
                difference,
                Transaction?.AccountTransId!!,
                Transaction?.AccountId!!
            )*/

           /* db.dao().readLastTransaction(Transaction?.AccountId!!).observe(this, {

                GlobalScope.launch(Dispatchers.Main) {
                    if (it == null) {
                        db.dao().updateAccountBalance(0.0, date, Transaction?.AccountId!!)
                        txtbalance.setText(0)
                    } else {
                        db.dao().updateAccountBalance(
                            it.Balance!!,
                            date,
                            it?.AccountId!!
                        )

                    }
                }
            })*/
            setData(Transaction?.AccountId!!)

            db.dao().readLastTransaction(Transaction?.AccountId!!).observe(this,{
                GlobalScope.launch(Dispatchers.Main) {
                    db.dao().updateAccountBalance(it.Balance!!,currentdate,Transaction?.AccountId!!)
                }

            })

            /*db.dao().readBalance(Transaction?.AccountId!!).observe(this){

            }*/
            val intent= Intent(this,TransactionActivity::class.java)
            intent.putExtra("Accountmodel", accountmodeldata)
            startActivity(intent)
            finish()


        }
        else
        {
            if(!categoryChanged) {
                tranactionmodel.updateTranscation(
                    type,
                    Amount,
                    (Transaction?.Balance?.plus(update!!)).toString().toDouble(),
                    update_trans_desc.getText().toString(),
                    Converters.DMYtoYMD(update_trans_date.getText().toString()),
                    currentdate,
                    Transaction?.AccountTransId!!
                )

                tranactionmodel.updateTrailingTransaction(
                    update!!,
                    Transaction?.AccountTransId!!,
                    Transaction?.AccountId!!
                )
            }else{
                // D->C	then difference is +(oldvalue + newvalue)
                // C->D	then difference is -(oldvalue + newvalue)
                var difference: Double = (Transaction?.Amount?.plus(Amount)!!)
                if(Transaction?.AccountTranType?.equals("CR")!!)
                    difference *= -1
                tranactionmodel.updateTranscation(
                    type,
                    Amount,
                    (Transaction?.Balance?.plus(difference)).toString().toDouble(),
                    update_trans_desc.getText().toString(),
                    Converters.DMYtoYMD(update_trans_date.getText().toString()),
                    currentdate,
                    Transaction?.AccountTransId!!
                )

                tranactionmodel.updateTrailingTransaction(
                    difference,
                    Transaction?.AccountTransId!!,
                    Transaction?.AccountId!!
                )

            }
        }


        /*db.dao().readLastTransaction(Transaction?.AccountId!!).observe(this,{
            GlobalScope.launch(Dispatchers.Main) {
                db.dao().updateAccountBalance(it.Balance!!,currentdate,Transaction?.AccountId!!)
            }

        })

        db.dao().readBalance(Transaction?.AccountId!!).observe(this){
            val intent= Intent(this,TransactionActivity::class.java)
            intent.putExtra("Accountmodel", it)
            startActivity(intent)
            finish()
        }*/



    }

    private fun setData(accountId: Int) {
//        val fomatdate:String= SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)
        val currentdate: String =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val model = TransAccount()
        model.AccountId = accountId
        model.AccountTransDate = Converters.DMYtoYMD(update_trans_date.text.toString())
        model.AccountTransModifiedDate = currentdate
        editamount=update_trans_amount.text?.toString()!!.replace("$", "").replace(",", "")
        model.Amount = editamount!!.toDouble()
        model.Description = update_trans_desc.text?.toString()


        if (Rbcr?.isChecked!!) {
            sharedPref.edit().putString("type", "CR").commit()
            //  Toast.makeText(this,AccountList?.get(0)?.Balance.toString(),Toast.LENGTH_SHORT).show()

            val amount: Double= editamount.toString().toDouble()
            var transid:Int=99999999
            var newbalance:Double=0.0
            var ret: TransAccount? = db.dao().readBalanceupto(accountId, model.AccountTransDate!!)
            if(ret!=null)
            {
                transid = ret?.AccountTransId!!
                newbalance=ret?.Balance!!
            }

            var newbal= 0.0

            GlobalScope.launch(Dispatchers.Main) {

                newbal = newbalance?.plus(amount)
                model.Balance = newbal
                model.AccountTranType = "CR"

                db.dao().updateTrailingTransaction_date(model.Amount!!,model.AccountTransDate!!,transid,model.AccountId!!)
                tranactionmodel.insert(model)

                if (newbal != null) {
                    accountmodel.updateAccountBalance(newbal, date.toString(), model?.AccountId!!)
                    db.dao().readBalance(model?.AccountId!!).observe(this@Update_Transaction_Activity) { acc ->
                        account = acc
                    }
                }

            }



        } else {
            sharedPref.edit().putString("type", "DR").commit()

            var transid:Int=99999999
            var newbalance:Double=0.0
            var ret: TransAccount? = db.dao().readBalanceupto(accountId, model.AccountTransDate!!)
            if(ret!=null)
            {
                transid = ret?.AccountTransId!!
                newbalance=ret?.Balance!!
            }

            var newbal= 0.0
            val amount: Double = editamount.toString().toDouble()
            newbal = newbalance.minus(amount)
            model.Balance = newbal
            model.AccountTranType = "DR"

            //db.dao().updateTrailingTransaction(model.Amount?.times(-1)!!, transid!!, model.AccountId!!)
            db.dao().updateTrailingTransaction_date(model.Amount?.times(-1)!!,model.AccountTransDate!!,transid,model.AccountId!!)
            GlobalScope.launch(Dispatchers.Main) {
                tranactionmodel.insert(model)
                if (newbal != null) {
                    accountmodel.updateAccountBalance(newbal, date.toString(), model?.AccountId!!)
                    db.dao().readBalance(model?.AccountId!!).observe(this@Update_Transaction_Activity) { acc ->
                        account = acc
                    }
                }

            }


        }
    }

    private fun loaddata(transaction: TransAccount?) {


        update_trans_date.setText(Converters.YMDtoDMY(transaction?.AccountTransDate.toString()))
        update_trans_amount.setText(transaction?.Amount.toString())
        update_trans_desc.setText(transaction?.Description)
        if(transaction?.AccountTranType.equals("CR"))
        {
            Rbcr.setChecked(true)
        }
        else
        {
            Rbdr.setChecked(true)
        }



    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) {
            val intent= Intent(this,TransactionActivity::class.java)
            intent.putExtra("Accountmodel", accountmodeldata)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}