package com.hafizhnotes.currencyconversion.ui.currency_rates

import androidx.lifecycle.LiveData
import com.hafizhnotes.currencyconversion.data.api.CurrencyLayerInterface
import com.hafizhnotes.currencyconversion.data.repository.CurrencyLiveDataSource
import com.hafizhnotes.currencyconversion.data.repository.NetworkState
import com.hafizhnotes.currencyconversion.data.vo.CurrencyLiveResponse
import io.reactivex.disposables.CompositeDisposable

class CurrencyRatesRepository(private val apiService: CurrencyLayerInterface) {
    private lateinit var currencyLivesDataSource: CurrencyLiveDataSource

    fun fetchSingleCurrencyLive(
        compositeDisposable: CompositeDisposable
    ): LiveData<CurrencyLiveResponse> {
        currencyLivesDataSource =
            CurrencyLiveDataSource(apiService, compositeDisposable)

        currencyLivesDataSource.fetchCurrencyLive()
        return currencyLivesDataSource.currencyLiveResponse
    }

    fun getCurrencyLiveNetworkState(): LiveData<NetworkState> =
        currencyLivesDataSource.networkState
}