package com.hafizhnotes.currencyconversion.data.api

import com.hafizhnotes.currencyconversion.data.constant.URLConstant
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val API_KEY = "0dd749e64851c409f533a9c2f6abb258"

object CurrencyLayerClient {

    fun getClient(): CurrencyLayerInterface {
        val requestInterceptor =
            Interceptor { chain ->
                val url = chain
                    .request()
                    .url
                    .newBuilder()
                    .addQueryParameter("access_key", API_KEY).build()

                val request = chain
                    .request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

        val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(requestInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit
            .Builder()
            .client(okHttpClient)
            .baseUrl(URLConstant.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyLayerInterface::class.java)
    }
}