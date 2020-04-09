package com.hafizhnotes.currencyconversion.data.vo


import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class CurrencyLiveResponse(
    @SerializedName("privacy")
    val privacy: String = "",

    @SerializedName("source")
    val source: String = "",

    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("terms")
    val terms: String = "",

    @SerializedName("timestamp")
    val timestamp: Int = 0,

    @SerializedName("quotes")
    var currencyRates: JsonObject = JsonObject()
)