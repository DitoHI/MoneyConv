package com.hafizhnotes.currencyconversion.data.constant

object URLConstant {
    const val BASE_URL = "http://api.currencylayer.com/"

    fun countryFlagUri(countryCode: String) =
        "https://www.countryflags.io/$countryCode/flat/64.png"
}