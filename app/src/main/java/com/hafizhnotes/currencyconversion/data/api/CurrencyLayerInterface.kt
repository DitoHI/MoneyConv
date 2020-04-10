package com.hafizhnotes.currencyconversion.data.api

import com.hafizhnotes.currencyconversion.data.vo.CurrencyListResponse
import com.hafizhnotes.currencyconversion.data.vo.CurrencyLiveResponse
import io.reactivex.Single
import retrofit2.http.GET

interface CurrencyLayerInterface {

    @GET("live")
    fun getCurrencyLive(): Single<CurrencyLiveResponse>

    @GET("list")
    fun getCurrencyList(): Single<CurrencyListResponse>
}