package com.example.expensemanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.expense_manager.database.Account
import com.example.expense_manager.database.Currency
import com.example.expensemanager.R
import com.example.expensemanager.model.CurrencyViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AccountAdapter(private var accountList: List<Account>):
    RecyclerView.Adapter<AccountAdapter.ViewHolder>()
{
    private lateinit var currencymodel: CurrencyViewModel
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var accounttxt: TextView = itemView.findViewById(R.id.txtaccountname)
        var balancetxt:TextView=itemView.findViewById(R.id.txtbalance)
        var datetxt:TextView=itemView.findViewById(R.id.txtdate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_recyclerview, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AccountAdapter.ViewHolder, position: Int) {
        val acc = accountList[position]
        holder.accounttxt.text=acc.AccountName
        holder.datetxt.text=acc.AccountCreatedDate
        

    }

    override fun getItemCount(): Int {
        return accountList.size
    }

}
