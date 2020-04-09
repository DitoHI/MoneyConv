package com.hafizhnotes.currencyconversion.ui.exchange_currency

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.hafizhnotes.currencyconversion.room.db.CurrencyLayerDatabase
import com.hafizhnotes.currencyconversion.room.repository.CurrencyListRoomRepository
import com.hafizhnotes.currencyconversion.room.vo.CurrencyListRoomResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExchangeCurrencyRoomViewModel(application: Application) : AndroidViewModel(application) {

    private val listDao = CurrencyLayerDatabase.getDatabase(application).currencyListRoomDao()

    private val repository = CurrencyListRoomRepository(listDao)

    val allCurrencyLive: LiveData<List<CurrencyListRoomResponse>> by lazy {
        repository.allCurrency
    }

    fun insert(currencyListRoomResponse: CurrencyListRoomResponse) =
        viewModelScope
            .launch(Dispatchers.IO) {
                repository.deleteAll()
                repository.insert(currencyListRoomResponse)
            }
}