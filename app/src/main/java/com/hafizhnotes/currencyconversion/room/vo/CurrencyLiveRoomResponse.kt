package com.hafizhnotes.currencyconversion.room.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hafizhnotes.currencyconversion.data.constant.DBConstant

@Entity(tableName = DBConstant.CURRENCY_LIVE_DB_NAME)
class CurrencyLiveRoomResponse(
    @PrimaryKey(autoGenerate = true) val id: Int,

    @ColumnInfo(name = DBConstant.CURRENCY_LIVE_COLUMN_PRIVACY)
    val privacy: String,

    @ColumnInfo(name = DBConstant.CURRENCY_LIVE_COLUMN_SOURCE)
    val source: String,

    @ColumnInfo(name = DBConstant.CURRENCY_LIVE_COLUMN_SUCCESS)
    val success: Boolean,

    @ColumnInfo(name = DBConstant.CURRENCY_LIVE_COLUMN_TERMS)
    val terms: String,

    @ColumnInfo(name = DBConstant.CURRENCY_LIVE_COLUMN_TIMESTAMP)
    val timestamp: Int,

    @ColumnInfo(name = DBConstant.CURRENCY_LIVE_COLUMN_CURRENCY_RATES)
    val currencyRates: String
)