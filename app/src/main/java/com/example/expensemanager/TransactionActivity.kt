package com.example.expensemanager



import android.Manifest
import android.R.attr.path
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.print.PDFPrint.OnPDFPrintListener
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TableLayout
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import com.google.android.material.internal.ContextUtils.getActivity
import com.hendrix.pdfmyxml.*
import com.hendrix.pdfmyxml.viewRenderer.AbstractViewRenderer
import com.tejpratapsingh.pdfcreator.utils.FileManager
import com.tejpratapsingh.pdfcreator.utils.PDFUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_transaction.*
import kotlinx.android.synthetic.main.add_transcation_custom_dialog.*
import kotlinx.android.synthetic.main.transaction_table_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.lang.reflect.Method
import java.text.NumberFormat
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
    var page:AbstractViewRenderer?=null
    private var account : Account? = null
    var table :TableLayout?=null
    lateinit var trans:ArrayList<TransAccount>
    lateinit var sharedPref: SharedPreferences
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)


        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        table=findViewById(R.id.tbllayout)

        MobileAds.initialize(this) {}
        adview()
        trans= arrayListOf()
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
        setupPermissions()


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
            txtbalanceview.setText(account?.CurrencySymbol + " " + bal)
            txtbalanceview.setTextColor(Color.parseColor("#ff0000"))
            txtbalanceview.alpha=0.7F
        }
        else
        {
            val bal=Integer.parseInt(account?.Balance!!.roundToInt().toString())
            txtbalanceview.setText(account?.CurrencySymbol + " " + bal)
            txtbalanceview.setTextColor(Color.parseColor("#008000"))
            txtbalanceview.alpha=0.7F
        }



        //fetchTransaction()
        val accid: Int? = account?.AccountId
        if (accid != null) {
            db.dao().readTransaction(accid).observe(this) { Transactions ->
                trans.addAll(Transactions)
                recycle_table.adapter = TransacationAdapter(Transactions as ArrayList<TransAccount>,
                    {
                        //Edit
                        val intent = Intent(this, Update_Transaction_Activity::class.java)
                        intent.putExtra("Transactionmodel", it)
                        startActivity(intent)
                        finish()
                    },
                    {
                        //Delete
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage("Are you sure you want to Delete?")
                            .setCancelable(false)
                            .setPositiveButton("Yes") { dialog, id ->
                                val date: String =
                                    SimpleDateFormat(
                                        "dd-MM-yyyy",
                                        Locale.getDefault()
                                    ).format(Date())
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
                                        if (it == null) {
                                            db.dao().updateAccountBalance(0.0, date, accid)
                                            txtbalance.setText(0)
                                        } else {
                                            db.dao().updateAccountBalance(
                                                it.Balance!!,
                                                date,
                                                it?.AccountId!!
                                            )
                                            txtbalance.setText(it.Balance!!.toString())

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

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("External", "Permission to record denied")
            makeRequest()
        }
    }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            101)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            101->{
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("External", "Permission has been denied by user")
                } else {
                    Log.i("EXTERNAL", "Permission has been granted by user")
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    private fun pdfgenrator(accountId: Int?):Uri? {

        var pdfUri:Uri?=null
        val sb = StringBuilder()
        sb.append("<html>")
        sb.append("<style>")
        sb.append("  table {border-collapse:collapse; table-layout:fixed; width:100%;-fs-table-paginate: paginate; page-break-after:always}")
        sb.append(" table td {border:solid 1px ; width:100px; word-wrap:break-word;}")
        sb.append(" table th {border:solid 1px ; width:100px; word-wrap:break-word;}")
        sb.append(
            "thead\n" +
                    "        {\n" +
                    "            display: table-header-group;\n" +
                    "        }\n" +
                    "        tfoot\n" +
                    "        {\n" +
                    "            display: table-footer-group;\n" +
                    "        }"
        )
        sb.append(
            "@media print {\n" +
                    ".page-break { display: block; page-break-before: always; }\n" +
                    "}"
        )
        sb.append(
            "@page {\n" +
                    "  size: A4;\n" +
                    "  margin: 11mm 17mm 17mm 17mm;\n" +
                    "}"
        )
        sb.append(
            "header{\n" +
                    "\n" +
                    " font-size: 50px;\n" +
                    "  color: #000;\n" +
                    "  text-align: center;\n" +
                    "}"
        )
        sb.append("</style>")
        sb.append("<body>")
        sb.append(
            "<header>\n" +
                    account?.AccountName +
                    "</header>"
        )
        sb.append("	<table>")
        sb.append("<thead>")
        sb.append("		<tr>")
        sb.append("			<th align=\"center\">Date</th>")
        sb.append("			<th align=\"left\">Description</th>")
        sb.append("			<th align=\"right\">Amount</th>")
        sb.append("			<th align=\"right\">Balance</th>")
        sb.append("		</tr>")
        sb.append("</thead>")


                for (transAccount in trans) {

                    if(transAccount.AccountTranType.equals("CR"))
                    {sb.append("		<tr bgcolor=#008000;opacity:0.9;>")
                        val bal=
                            NumberFormat.getInstance().format(transAccount.Balance!!.roundToInt()).toString()
                        sb.append("			<td align=\"center\">" + transAccount.AccountTransDate + "</td>")
                        sb.append("			<td align=\"left\">" + transAccount.Description + "</td>")
                        sb.append(
                            "			<td align=\"right\">" + NumberFormat.getInstance().format(
                                transAccount.Amount
                            ).toString() + "</td>"
                        )
                        sb.append("			<td align=\"right\">" + bal + "</td>")
                    }
                    else
                    {
                        sb.append("		 <tr bgcolor=#ff0000;opacity:0.9;>")
                        val bal= NumberFormat.getInstance().format(transAccount.Balance!!.roundToInt()).toString().drop(
                            1
                        )
                        sb.append("			<td align=\"center\">" + transAccount.AccountTransDate + "</td>")
                        sb.append("			<td align=\"left\">" + transAccount.Description + "</td>")
                        sb.append(
                            "			<td align=\"right\">" + NumberFormat.getInstance().format(
                                transAccount.Amount
                            ).toString() + "</td>"
                        )
                        sb.append("			<td align=\"right\">" + bal + "</td>")
                    }

                    sb.append("		</tr>")
                }


        sb.append("	</table>")

        sb.append("</body>")
        sb.append("</html>")
        Log.d("HTML", sb.toString())
        FileManager.getInstance().cleanTempFolder(applicationContext)
        // Create Temp File to save Pdf To
        val savedPDFFile =
            
            FileManager.getInstance().createTempFile(applicationContext, "pdf", false)
        // Generate Pdf From Html
        // Generate Pdf From Html
        PDFUtil.generatePDFFromHTML(
            applicationContext,
            savedPDFFile,
            sb.toString().trimIndent(),
            object : OnPDFPrintListener {
                override fun onSuccess(file: File) {
                    // Open Pdf Viewer
                    pdfUri = Uri.fromFile(savedPDFFile)
                    Log.d("PDF", pdfUri.toString())


                }

                override fun onError(exception: Exception) {
                    exception.printStackTrace()
                }
            })
        return Uri.fromFile(savedPDFFile)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.pdf_menu, menu);
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.getItemId() === android.R.id.home) {
            finish()
            return true
        }
        else if(item.getItemId()===R.id.view_pdf) {
            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    val m: Method =
                        StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                    m.invoke(null)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            val pdfuri: Uri? = pdfgenrator(account?.AccountId)
            val pdfIntent = Intent(Intent.ACTION_VIEW)

            pdfIntent.setDataAndType(pdfuri, "application/pdf");
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            try {
                startActivity(pdfIntent)
            } catch (e: java.lang.Exception) {
                Toast.makeText(
                    this@TransactionActivity, "No Application available to viewPDF",
                    Toast.LENGTH_SHORT
                ).show();
            }

        }
        else if(item.getItemId()===R.id.share_pdf)
        {
            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    val m: Method =
                        StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                    m.invoke(null)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            val pdfuri: Uri? = pdfgenrator(account?.AccountId)
            val shareintent = Intent(Intent.ACTION_SEND)
            shareintent.setDataAndType(pdfuri, "application/pdf")
            shareintent.putExtra(Intent.EXTRA_STREAM,pdfuri)
            startActivity(Intent.createChooser(shareintent,"Share Using"))
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
                Toast.makeText(this, "Enter Amount", LENGTH_SHORT).show()
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

            Toast.makeText(this, "Transcation Completed", LENGTH_SHORT).show()
            txtbalanceview.setText(account?.CurrencySymbol + " " + newbal)
            if(newbal!! <0)
            {
                txtbalanceview.setTextColor(Color.parseColor("#ff0000"))
                txtbalanceview.alpha=0.7F
            }
            else
            {
                txtbalanceview.setTextColor(Color.parseColor("#008000"))
                txtbalanceview.alpha=0.7F
            }
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
            Toast.makeText(this, "Transcation Completed", LENGTH_SHORT).show()
            txtbalanceview.setText(account?.CurrencySymbol + " " + newbal)
            if(newbal!! <0)
            {
                txtbalanceview.setTextColor(Color.parseColor("#ff0000"))
                txtbalanceview.alpha=0.7F
            }
            else
            {
                txtbalanceview.setTextColor(Color.parseColor("#008000"))
                txtbalanceview.alpha=0.7F
            }
            dialog?.dismiss()
        }
    }


}