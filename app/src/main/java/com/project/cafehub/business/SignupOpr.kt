package com.project.cafehub.business

import java.util.*

class SignupOpr {
    public fun getAge(day:Int, month:Int, year:Int): Int {
        val c1 = Calendar.getInstance()
        c1[year, month - 1, day, 0] = 0 // as MONTH in calender is 0 based.
        val c2 = Calendar.getInstance()
        var diff = c2[Calendar.YEAR] - c1[Calendar.YEAR]
        if (c1[Calendar.MONTH] > c2[Calendar.MONTH] ||
            c1[Calendar.MONTH] == c2[Calendar.MONTH] && c1[Calendar.DATE] > c2[Calendar.DATE]
        ) {
            diff--
        }
        return diff
    }
}