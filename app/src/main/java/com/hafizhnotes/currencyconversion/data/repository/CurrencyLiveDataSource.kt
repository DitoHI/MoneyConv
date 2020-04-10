package com.hafizhnotes.currencyconversion.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hafizhnotes.currencyconversion.data.api.CurrencyLayerInterface
import com.hafizhnotes.currencyconversion.data.constant.ErrorConstant
import com.hafizhnotes.currencyconversion.data.vo.CurrencyLiveResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class CurrencyLiveDataSource(
    private val apiService: CurrencyLayerInterface,
    private val compositeDisposable: CompositeDisposable
) {

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _currencyLiveResponse = MutableLiveData<CurrencyLiveResponse>()
    val currencyLiveResponse: LiveData<CurrencyLiveResponse>
        get() = _currencyLiveResponse

    fun fetchCurrencyLive() {
        _networkState.postValue(NetworkState.LOADING)

        try {
            compositeDisposable.add(
                apiService
                    .getCurrencyLive()
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            _currencyLiveResponse.postValue(it)
                            _networkState.postValue(NetworkState.LOADED)
                        },
                        {
                            _networkState
                                .postValue(
                                    NetworkState(
                                        Status.FAILED,
                                        it.message ?: ErrorConstant.ERROR_UNDEFINED
                                    )
                                )
                        }
                    )
            )
        } catch (e: Exception) {
            _networkState
                .postValue(
                    NetworkState(
                        Status.FAILED,
                        e.message ?: ErrorConstant.ERROR_UNDEFINED
                    )
                )
        }
    }
}