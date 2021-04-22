package com.example.expensemanager

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.TransAccount
import com.example.expensemanager.model.AccountViewModel
import com.example.expensemanager.model.TransactionViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AddTransactionActivity : AppCompatActivity() {



    private lateinit var transactionmodel: TransactionViewModel
    private lateinit var accountmodel: AccountViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        transactionmodel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        accountmodel = ViewModelProvider(this).get(AccountViewModel::class.java)

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = sdf.format(Date())
        datepick.setText(currentDate)
        val materialDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .build()

        val account = getIntent().getParcelableExtra<Account>("Accountmodel")
        datepick.setOnClickListener {
            materialDatePicker.show(getSupportFragmentManager(), "Datepickerdialog")
            materialDatePicker.addOnPositiveButtonClickListener { selection -> // Get the offset from our timezone and UTC.
                val timeZoneUTC = TimeZone.getDefault()
                // It will be negative, so that's the -1
                val offsetFromUTC = timeZoneUTC.getOffset(Date().time) * -1
                // Create a date format, then a date object with our offset
                val simpleFormat = SimpleDateFormat("dd/MM/yyyy")
                val date = Date(selection + offsetFromUTC)
                datepick.setText(simpleFormat.format(date))
            }
        }
        btn_cancel.setOnClickListener {
            val intent = Intent(this, TransactionActivity::class.java)
            finish()
            startActivity(intent)

        }

        btn_ok.setOnClickListener {
            if(TextUtils.isEmpty(edit_amount.text))
            {
                Toast.makeText(this,"Enter Amount", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val date: String =
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                val model = TransAccount()
                model.AccountId= account?.AccountId!!
                model.AccountTransDate= date
                model.AccountTransModifiedDate= date
                model.Amount= Integer.parseInt(edit_amount.text.toString()).toDouble()
                model.Description=edit_desc.text.toString()



                if(Rbcr.isChecked())
                {
                    val bal:Double?= account.Balance
                    val amount: Int =Integer.parseInt(edit_amount.text.toString())
                    val newbal= bal?.plus(amount)
                    model.Balance=newbal
                    model.AccountTranType="CR"
                    GlobalScope.launch(Dispatchers.Main) {
                        transactionmodel.insert(model)
                        if (newbal != null) {
                            accountmodel.updateAccountBalance(newbal,account.AccountId)
                        }

                    }

                    Toast.makeText(this,"Transcation Completed",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, TransactionActivity::class.java)
                    finish()
                    startActivity(intent)


                }
                else
                {
                    val bal:Double?= account.Balance
                    val amount: Int =Integer.parseInt(edit_amount.text.toString())
                    val newbal= bal?.minus(amount)
                    model.Balance=newbal
                    model.AccountTranType="DR"
                    GlobalScope.launch(Dispatchers.Main) {
                        transactionmodel.insert(model)
                        if (newbal != null) {
                            accountmodel.updateAccountBalance(newbal,account.AccountId)
                        }

                    }
                    Toast.makeText(this,"Transcation Completed",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, TransactionActivity::class.java)
                    finish()
                    startActivity(intent)

                }
            }
        }


    }




}


