package com.hafizhnotes.currencyconversion.data.vo

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.hafizhnotes.currencyconversion.room.vo.CurrencyListRoomResponse

data class CurrencyListResponse(
    @SerializedName("currencies")
    var currencies: JsonObject = JsonObject(),

    @SerializedName("privacy")
    val privacy: String = "",

    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("terms")
    val terms: String = ""
) {

    companion object {

        fun fromRoomResponse(roomResponse: CurrencyListRoomResponse): CurrencyListResponse {
            return CurrencyListResponse(
                privacy = roomResponse.privacy,
                success = roomResponse.success,
                terms = roomResponse.terms,
                currencies = Gson().fromJson(roomResponse.currencies, JsonObject::class.java)
            )
        }
    }

    fun toRoomResponse(): CurrencyListRoomResponse {
        return CurrencyListRoomResponse(
            privacy = privacy,
            success = success,
            terms = terms,
            currencies = Gson().toJson(currencies)
        )
    }
}