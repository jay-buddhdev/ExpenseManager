package com.example.expense_manager.database

import java.text.SimpleDateFormat
import java.util.*

class Converters {
    companion object
    {
        fun DMYtoYMD(date:String):String
        {
            val initDate = SimpleDateFormat("dd/MM/yyyy").parse(date)
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val parsedDate = formatter.format(initDate)
            return parsedDate

            /*val dateformat: String =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(date)
            return  dateformat*/
        }
        fun YMDtoDMY(date:String):String
        {

            val initDate = SimpleDateFormat("yyyy-MM-dd").parse(date)
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val parsedDate = formatter.format(initDate)
            return parsedDate
        }

    }



}
