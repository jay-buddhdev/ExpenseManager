package com.example.expensemanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.expense_manager.database.Currency
import com.example.expensemanager.R
import java.util.ArrayList

class CurrencyAdapter(private var currencyList: List<Currency?>?,
                      private val itemClickCallBack: (currency: Currency) -> Unit ) :
        RecyclerView.Adapter<CurrencyAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var currencytxt: TextView = view.findViewById(R.id.txtCurrencyrecyler)
        

    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.currency_recyclerview, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val current = currencyList?.get(position)
        holder.currencytxt.text = current?.CurrencyName+" - "+current?.CurrencySymbol

        holder.itemView.setOnClickListener {
            if (current != null) {
                itemClickCallBack(current)
            }
        }

    }

    override fun getItemCount(): Int {
        if(currencyList==null)
        {
            return 0
        }
        else
        {
        return currencyList?.size!!
            }
    }

}