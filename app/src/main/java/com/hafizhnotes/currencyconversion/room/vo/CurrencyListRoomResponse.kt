package com.hafizhnotes.currencyconversion.room.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hafizhnotes.currencyconversion.data.constant.DBConstant

@Entity(tableName = DBConstant.CURRENCY_LIST_DB_NAME)
class CurrencyListRoomResponse(
    @PrimaryKey(autoGenerate = true) val id: Int,

    @ColumnInfo(name = DBConstant.CURRENCY_LIST_COLUMN_CURRENCIES)
    val currencies: String,

    @ColumnInfo(name = DBConstant.CURRENCY_LIST_COLUMN_PRIVACY)
    val privacy: String,

    @ColumnInfo(name = DBConstant.CURRENCY_LIST_COLUMN_SUCCESS)
    val success: Boolean,

    @ColumnInfo(name = DBConstant.CURRENCY_LIST_COLUMN_TERMS)
    val terms: String
)