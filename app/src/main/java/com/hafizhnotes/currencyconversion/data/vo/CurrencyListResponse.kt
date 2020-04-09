package com.hafizhnotes.currencyconversion.data.vo

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class CurrencyListResponse(
    @SerializedName("currencies")
    var currencies: JsonObject = JsonObject(),

    @SerializedName("privacy")
    val privacy: String = "",

    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("terms")
    val terms: String = ""
)