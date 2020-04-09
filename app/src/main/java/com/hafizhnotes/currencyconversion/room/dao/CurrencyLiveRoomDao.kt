package com.hafizhnotes.currencyconversion.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hafizhnotes.currencyconversion.data.constant.DBConstant
import com.hafizhnotes.currencyconversion.room.vo.CurrencyLiveRoomResponse

@Dao
interface CurrencyLiveRoomDao {
    @Query("SELECT * FROM ${DBConstant.CURRENCY_LIVE_DB_NAME} ORDER BY timestamp DESC")
    fun getLatest(): LiveData<List<CurrencyLiveRoomResponse>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(currencyLiveResponse: CurrencyLiveRoomResponse)
}