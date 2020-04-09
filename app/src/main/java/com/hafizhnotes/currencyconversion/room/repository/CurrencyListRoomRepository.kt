package com.hafizhnotes.currencyconversion.room.repository

import androidx.lifecycle.LiveData
import com.hafizhnotes.currencyconversion.room.dao.CurrencyListRoomDao
import com.hafizhnotes.currencyconversion.room.vo.CurrencyListRoomResponse

class CurrencyListRoomRepository(private val currencyListRoomDao: CurrencyListRoomDao) {

    val allCurrency: LiveData<List<CurrencyListRoomResponse>> =
        currencyListRoomDao.getAll()

    suspend fun insert(currencyListRoomResponse: CurrencyListRoomResponse) {
        currencyListRoomDao.insert(currencyListRoomResponse)
    }

    suspend fun deleteAll() = currencyListRoomDao.deleteAll()
}