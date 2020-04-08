package com.hafizhnotes.currencyconversion.ui.currency_rates

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hafizhnotes.currencyconversion.data.repository.NetworkState
import com.hafizhnotes.currencyconversion.data.vo.CurrencyLiveResponse
import io.reactivex.disposables.CompositeDisposable

class CurrencyRatesViewModel(
    private val ratesRepository: CurrencyRatesRepository
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val currencyLive: LiveData<CurrencyLiveResponse> by lazy {
        ratesRepository.fetchSingleCurrencyLive(compositeDisposable)
    }

    val networkState: LiveData<NetworkState> by lazy {
        ratesRepository.getCurrencyLiveNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}