package com.hafizhnotes.currencyconversion.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hafizhnotes.currencyconversion.data.api.CurrencyLayerInterface
import com.hafizhnotes.currencyconversion.data.constant.ErrorConstant
import com.hafizhnotes.currencyconversion.data.vo.CurrencyListResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class CurrencyListDataSource(
    private val apiService: CurrencyLayerInterface,
    private val compositeDisposable: CompositeDisposable
) {

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _currencyListResponse = MutableLiveData<CurrencyListResponse>()
    val currencyListResponse: LiveData<CurrencyListResponse>
        get() = _currencyListResponse

    fun fetchCurrencyList() {
        _networkState.postValue(NetworkState.LOADING)
        try {
            compositeDisposable.add(
                apiService
                    .getCurrencyList()
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            _currencyListResponse.postValue(it)
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