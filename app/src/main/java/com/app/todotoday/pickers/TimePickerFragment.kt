package com.app.myapplication.fragments.Pickers

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment(val listener: (String)->Unit):DialogFragment(),TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return TimePickerDialog(activity as Context, this, hour, minute, false)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val t = hourOfDay.toString()
        var m=minute.toString()
        when(m){
            "1"->m="01"
            "2"->m="02"
            "3"->m="03"
            "4"->m="04"
            "5"->m="05"
            "6"->m="06"
            "7"->m="07"
            "8"->m="08"
            "9"->m="09"
        }
        listener("$t:$m")
    }
}