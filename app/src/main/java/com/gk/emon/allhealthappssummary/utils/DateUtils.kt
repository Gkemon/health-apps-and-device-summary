package com.gk.emon.allhealthappssummary.utils

import java.util.*

object DateUtils {
    fun getCalenderFromTodayToOneYearBack(): Calendar {
        val startDate = Calendar.getInstance()
        startDate.add(Calendar.YEAR, -1)
        return startDate
    }
}