package com.example.expensemanager.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.expense_manager.database.Converters
import com.example.expense_manager.database.TransAccount
import com.example.expensemanager.R
import java.text.NumberFormat
import kotlin.math.abs
import kotlin.math.roundToInt

class TransacationAdapter(private var transactionList:ArrayList<TransAccount>,
                          private val itemEditCallBack:(transaction: TransAccount)->Unit,
                          private val itemDeleteCallBack:(transaction: TransAccount)->Unit
):
    RecyclerView.Adapter<TransacationAdapter.ViewHolder>()
{
    private val viewBinderHelper = ViewBinderHelper()
    private var context: Context? = null


    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var datetxt: TextView = itemView.findViewById(R.id.txttransdate)
        var baltxt:TextView=itemView.findViewById(R.id.txttransbal)
        var amount:TextView=itemView.findViewById(R.id.txttransamt)
        var des:TextView=itemView.findViewById(R.id.txttransdes)
        var tblrow:TableRow=itemView.findViewById(R.id.tblrow)
        var imgedit: ImageButton =itemView.findViewById(R.id.edit_button)
        var imgdelete: ImageButton =itemView.findViewById(R.id.delete_button)
        val swipelayout : SwipeRevealLayout = itemView.findViewById(R.id.swipe_layout)
        private var viewBinderHelper:ViewBinderHelper?=null


        init {
            viewBinderHelper= ViewBinderHelper()
            viewBinderHelper?.setOpenOnlyOne(true)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_table_layout, parent, false)
        return TransacationAdapter.ViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactionList[position]
        viewBinderHelper.bind(holder.swipelayout, transaction.AccountTransId.toString())

        if (transaction.Balance!! < 0) {
            holder.baltxt.text = (NumberFormat.getInstance()
                .format(abs(transaction.Balance!!.roundToInt()))).toString() + " " + "DR"
        } else {
            holder.baltxt.text = (NumberFormat.getInstance()
                .format(abs(transaction.Balance!!.roundToInt()))).toString() + " " + "CR"
        }

        if (transaction.AccountTranType.equals("CR")) {
            val bal = NumberFormat.getInstance().format(abs(transaction.Balance!!.roundToInt()))
                .toString()

            //old colour #90ee90
            holder.tblrow.setBackgroundColor(Color.parseColor("#008000"))
            holder.tblrow.alpha = 0.7F
        } else {
            //val bal= NumberFormat.getInstance().format(transaction.Balance!!.roundToInt()).toString().drop(1)


            //old colour ##ec7f7f
            holder.tblrow.setBackgroundColor(Color.parseColor("#ff0000"))
            holder.tblrow.alpha = 0.7F
        }

        holder.amount.text = NumberFormat.getInstance().format(transaction.Amount).toString()
        holder.datetxt.text = Converters.YMDtoDMY(transaction.AccountTransDate.toString())

        holder.des.text = transaction.Description

        holder.imgedit.setOnClickListener {
            itemEditCallBack(transaction)
        }
        holder.imgdelete.setOnClickListener {

            itemDeleteCallBack(transaction)

        }

    }

    override fun getItemCount(): Int {
        return transactionList.size
    }
    fun getViewBinder()=viewBinderHelper
}