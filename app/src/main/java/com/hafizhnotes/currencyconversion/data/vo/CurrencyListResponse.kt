package com.hafizhnotes.currencyconversion.data.vo


import com.google.gson.annotations.SerializedName

data class CurrencyListResponse(
    @SerializedName("currencies")
    val currencies: Currencies = Currencies(),
    @SerializedName("privacy")
    val privacy: String = "",
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("terms")
    val terms: String = ""
)