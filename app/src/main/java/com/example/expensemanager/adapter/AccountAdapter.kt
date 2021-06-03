package com.example.expensemanager.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.expense_manager.database.Account
import com.example.expensemanager.R
import com.example.expensemanager.model.AccountViewModel
import com.example.expensemanager.model.TransactionViewModel
import kotlin.math.roundToInt


class AccountAdapter(
    private var accountList: List<Account>,
    private val itemClickCallBack: (currency: Account) -> Unit,
    private val itemEditCallBack:(currency:Account)->Unit,
    private val itemDeleteCallBack:(currency:Account)->Unit
):
    RecyclerView.Adapter<AccountAdapter.ViewHolder>()
{

    private val viewBinderHelper = ViewBinderHelper()

    private var context: Context? = null

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var accounttxt: TextView = itemView.findViewById(R.id.txtaccountname)
        var balancetxt:TextView=itemView.findViewById(R.id.txtbalance)
        var datetxt:TextView=itemView.findViewById(R.id.txtdate)
        val swipelayout : SwipeRevealLayout = itemView.findViewById(R.id.swipe_layout)
        var imgedit:ImageButton=itemView.findViewById(R.id.edit_button)
        var imgdelete:ImageButton=itemView.findViewById(R.id.delete_button)
        var item:CardView=itemView.findViewById(R.id.item_cardview)




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

            holder.balancetxt.text=  acc.CurrencySymbol+" "+bal.toString()+" "+"DR"
            holder.balancetxt.setTextColor(Color.parseColor("#ec7f7f"))
        }
        else
        {
            val bal=Integer.parseInt(acc.Balance!!.roundToInt().toString())

            holder.balancetxt.text=  acc.CurrencySymbol+" "+bal.toString()+" "+"CR"
            holder.balancetxt.setTextColor(Color.parseColor("#90ee90"))
        }
        viewBinderHelper.bind(holder.swipelayout, acc.AccountId.toString())
        holder.imgedit.setOnClickListener{
            itemEditCallBack(acc)
        }
        holder.imgdelete.setOnClickListener {

            itemDeleteCallBack(acc)

        }
        holder.item.setOnClickListener {
            itemClickCallBack(acc)
        }
        holder.itemView.setOnClickListener {

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


