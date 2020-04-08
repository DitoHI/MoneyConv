package com.hafizhnotes.currencyconversion.ui.exchange_currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hafizhnotes.currencyconversion.data.repository.NetworkState
import com.hafizhnotes.currencyconversion.data.vo.CurrencyListResponse
import io.reactivex.disposables.CompositeDisposable

class ExchangeCurrencyViewModel(
    private val exchangeCurrencyRepository: ExchangeCurrencyRepository
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val currencyList: LiveData<CurrencyListResponse> by lazy {
        exchangeCurrencyRepository.fetchSingleCurrencyList(compositeDisposable)
    }

    val networkState: LiveData<NetworkState> by lazy {
        exchangeCurrencyRepository.getCurrencyListNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}