package com.hafizhnotes.currencyconversion.ui.currency_rates

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.hafizhnotes.currencyconversion.room.db.CurrencyLayerDatabase
import com.hafizhnotes.currencyconversion.room.repository.CurrencyLiveRoomRepository
import com.hafizhnotes.currencyconversion.room.vo.CurrencyLiveRoomResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrencyRatesRoomViewModel(application: Application) : AndroidViewModel(application) {

    private val liveDao = CurrencyLayerDatabase.getDatabase(application).currencyLiveRoomDao()

    private val repository = CurrencyLiveRoomRepository(liveDao)

    val latestCurrencyLive: LiveData<List<CurrencyLiveRoomResponse>> by lazy {
        repository.latestCurrencyLive
    }

    fun insert(currencyLiveRoomResponse: CurrencyLiveRoomResponse) =
        viewModelScope
            .launch(Dispatchers.IO) {
                repository.deleteAll()
                repository.insert(currencyLiveRoomResponse)
            }
}