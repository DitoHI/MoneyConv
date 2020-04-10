package com.hafizhnotes.currencyconversion.data.helper

import java.sql.Timestamp
import java.util.*

class DateTimeHelper {

    companion object {

        fun unixToDate(timestamp: Long): Date {
            val sdf = Timestamp(timestamp)
            return Date(sdf.time)
        }
    }
}