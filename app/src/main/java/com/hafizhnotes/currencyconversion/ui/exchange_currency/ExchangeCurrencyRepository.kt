package com.hafizhnotes.currencyconversion.ui.exchange_currency

import androidx.lifecycle.LiveData
import com.hafizhnotes.currencyconversion.data.api.CurrencyLayerInterface
import com.hafizhnotes.currencyconversion.data.repository.CurrencyListDataSource
import com.hafizhnotes.currencyconversion.data.repository.NetworkState
import com.hafizhnotes.currencyconversion.data.vo.CurrencyListResponse
import io.reactivex.disposables.CompositeDisposable

class ExchangeCurrencyRepository(private val apiService: CurrencyLayerInterface) {

    private lateinit var currencyListDataSource: CurrencyListDataSource

    fun fetchSingleCurrencyList(
        compositeDisposable: CompositeDisposable
    ): LiveData<CurrencyListResponse> {
        currencyListDataSource =
            CurrencyListDataSource(apiService, compositeDisposable)

        currencyListDataSource.fetchCurrencyList()
        return currencyListDataSource.currencyListResponse
    }

    fun getCurrencyListNetworkState(): LiveData<NetworkState> =
        currencyListDataSource.networkState
}