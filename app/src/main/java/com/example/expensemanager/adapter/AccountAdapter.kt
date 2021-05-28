package com.example.expensemanager.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.Currency
import com.example.expense_manager.database.RoomDatabase
import com.example.expensemanager.R
import com.example.expensemanager.TransactionActivity
import com.example.expensemanager.model.CurrencyViewModel
import java.io.Serializable
import kotlin.math.roundToInt


class AccountAdapter(private var accountList: List<Account>,
                     private val itemClickCallBack: (currency: Account) -> Unit ):
    RecyclerView.Adapter<AccountAdapter.ViewHolder>()
{
    private var context: Context? = null

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var accounttxt: TextView = itemView.findViewById(R.id.txtaccountname)
        var balancetxt:TextView=itemView.findViewById(R.id.txtbalance)
        var datetxt:TextView=itemView.findViewById(R.id.txtdate)




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        context = parent.getContext()
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_recyclerview, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val acc = accountList[position]
        holder.accounttxt.text=acc.AccountName
        holder.datetxt.text=acc.AccountCreatedDate
        if(acc.Balance!! <0)
        {
            val bal= acc.Balance!!.roundToInt().toString().drop(1)

            holder.balancetxt.text=  acc.CurrencySymbol+" "+bal+" "+"DR"
            holder.balancetxt.setTextColor(Color.parseColor("#ec7f7f"))
        }
        else
        {
            val bal=Integer.parseInt(acc.Balance!!.roundToInt().toString())

            holder.balancetxt.text=  acc.CurrencySymbol+" "+bal+" "+"CR"
            holder.balancetxt.setTextColor(Color.parseColor("#90ee90"))
        }


        holder.itemView.setOnClickListener {
            itemClickCallBack(acc)
        }

        

    }

    /*private fun searchCurrency(currencyId: Int?) {
        if (currencyId != null) {
            db.dao().getcurrencysymbol(currencyId).observe(,observer{

            })
        }


    }*/



    override fun getItemCount(): Int {
        return accountList.size
    }

}


