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
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.TransAccount
import com.example.expensemanager.R

class TransacationAdapter(private var transactionList:ArrayList<TransAccount>,
                          private val itemEditCallBack:(transaction: TransAccount)->Unit,
                          private val itemDeleteCallBack:(transaction: TransAccount)->Unit):
    RecyclerView.Adapter<TransacationAdapter.ViewHolder>() {
    private var context: Context? = null
    private val viewBinderHelper = ViewBinderHelper()

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var datetxt: TextView = itemView.findViewById(R.id.txttransdate)
        var baltxt:TextView=itemView.findViewById(R.id.txttransbal)
        var amount:TextView=itemView.findViewById(R.id.txttransamt)
        var des:TextView=itemView.findViewById(R.id.txttransdes)
        var tblrow:TableRow=itemView.findViewById(R.id.tblrow)
        var imgedit: ImageButton =itemView.findViewById(R.id.edit_button)
        var imgdelete: ImageButton =itemView.findViewById(R.id.delete_button)
        val swipelayout : SwipeRevealLayout = itemView.findViewById(R.id.swipe_layout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.getContext()
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_table_layout, parent, false)
        return TransacationAdapter.ViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction=transactionList[position]
        if(transaction.AccountTranType.equals("CR"))
        {
            holder.tblrow.setBackgroundColor(Color.parseColor("#90ee90"))
        }
        else
        {
            holder.tblrow.setBackgroundColor(Color.parseColor("#ec7f7f"))
        }
        viewBinderHelper.bind(holder.swipelayout, transaction.AccountTransId.toString())
        holder.amount.setText(transaction.Amount.toString())
        holder.datetxt.setText(transaction.AccountTransDate.toString())
        holder.baltxt.setText(transaction.Balance.toString())
        holder.des.setText(transaction.Description)

        holder.imgedit.setOnClickListener{
            itemEditCallBack(transaction)
        }
        holder.imgdelete.setOnClickListener {

            itemDeleteCallBack(transaction)

        }

    }

    override fun getItemCount(): Int {
        return transactionList.size
    }
}