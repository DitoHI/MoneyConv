package com.hafizhnotes.currencyconversion.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hafizhnotes.currencyconversion.data.constant.DBConstant
import com.hafizhnotes.currencyconversion.room.vo.CurrencyListRoomResponse

@Dao
interface CurrencyListRoomDao {
    @Query("SELECT * FROM ${DBConstant.CURRENCY_LIST_DB_NAME} ORDER BY id DESC")
    fun getAll(): LiveData<List<CurrencyListRoomResponse>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(currencyListResponse: CurrencyListRoomResponse)

    @Query(
        "DELETE FROM ${DBConstant.CURRENCY_LIST_DB_NAME}"
    )
    suspend fun deleteAll()
}