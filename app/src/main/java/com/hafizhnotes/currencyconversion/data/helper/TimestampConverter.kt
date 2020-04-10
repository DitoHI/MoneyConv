package com.hafizhnotes.currencyconversion.data.helper

import androidx.room.TypeConverter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object TimestampConverter {
    private val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)

    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: String): Date {
        if (value.isEmpty()) return Calendar.getInstance().time

        return try {
            val timeZone = TimeZone.getTimeZone("IST")
            df.timeZone = timeZone
            df.parse(value) ?: Calendar.getInstance(timeZone).time
        } catch (e: ParseException) {
            Calendar.getInstance(TimeZone.getTimeZone("IST")).time
        }
    }

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(value: Date): String {
        val timeZone = TimeZone.getTimeZone("IST")
        df.timeZone = timeZone
        return df.format(value)
    }
}