package com.example.expensemanager.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expense_manager.database.TransAccount
import com.example.expensemanager.R

class TransacationAdapter(private var transactionList:List<TransAccount>):
    RecyclerView.Adapter<TransacationAdapter.ViewHolder>() {
    private var context: Context? = null

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var datetxt: TextView = itemView.findViewById(R.id.txttransdate)
        var baltxt:TextView=itemView.findViewById(R.id.txttransbal)
        var amount:TextView=itemView.findViewById(R.id.txttransamt)
        var des:TextView=itemView.findViewById(R.id.txttransdes)
        var tblrow:TableRow=itemView.findViewById(R.id.tblrow)

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
        holder.amount.setText(transaction.Amount.toString())
        holder.datetxt.setText(transaction.AccountTransDate.toString())
        holder.baltxt.setText(transaction.Balance.toString())
        holder.des.setText(transaction.Description)

    }

    override fun getItemCount(): Int {
        return transactionList.size
    }
}