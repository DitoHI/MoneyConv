package com.hafizhnotes.currencyconversion.room.repository

import androidx.lifecycle.LiveData
import com.hafizhnotes.currencyconversion.room.dao.CurrencyLiveRoomDao
import com.hafizhnotes.currencyconversion.room.vo.CurrencyLiveRoomResponse

class CurrencyLiveRoomRepository(private val currencyLiveRoomDao: CurrencyLiveRoomDao) {

    val latestCurrencyLive: LiveData<List<CurrencyLiveRoomResponse>> =
        currencyLiveRoomDao.getLatest()

    suspend fun insert(currencyLiveRoomResponse: CurrencyLiveRoomResponse) {
        currencyLiveRoomDao.insert(currencyLiveRoomResponse)
    }
}