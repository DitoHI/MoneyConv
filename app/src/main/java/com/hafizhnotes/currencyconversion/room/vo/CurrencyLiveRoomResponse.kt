package com.hafizhnotes.currencyconversion.room.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.hafizhnotes.currencyconversion.data.constant.DBConstant
import com.hafizhnotes.currencyconversion.data.helper.TimestampConverter
import java.time.OffsetDateTime
import java.util.*

@Entity(tableName = DBConstant.CURRENCY_LIVE_DB_NAME)
class CurrencyLiveRoomResponse(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo(name = DBConstant.CURRENCY_LIVE_COLUMN_PRIVACY)
    val privacy: String = "",

    @ColumnInfo(name = DBConstant.CURRENCY_LIVE_COLUMN_SOURCE)
    val source: String = "",

    @ColumnInfo(name = DBConstant.CURRENCY_LIVE_COLUMN_SUCCESS)
    val success: Boolean = false,

    @ColumnInfo(name = DBConstant.CURRENCY_LIVE_COLUMN_TERMS)
    val terms: String = "",

    @ColumnInfo(name = DBConstant.CURRENCY_LIVE_COLUMN_TIMESTAMP)
    val timestamp: Int = 0,

    @ColumnInfo(name = DBConstant.CURRENCY_LIVE_COLUMN_CURRENCY_RATES)
    val currencyRates: String = "",

    @ColumnInfo(name = DBConstant.CURRENCY_LIVE_COLUMN_CREATED_AT)
    val createdAt: Date = Calendar.getInstance(TimeZone.getTimeZone("IST")).time
)