package com.hafizhnotes.currencyconversion.data.vo


import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.hafizhnotes.currencyconversion.room.vo.CurrencyLiveRoomResponse

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
) {
    fun toRoomResponse(): CurrencyLiveRoomResponse {
        return CurrencyLiveRoomResponse(
            privacy = privacy,
            source = source,
            success = success,
            terms = terms,
            timestamp = timestamp,
            currencyRates = Gson().toJson(currencyRates)
        )
    }

    companion object {
        fun fromRoomResponse(roomResponse: CurrencyLiveRoomResponse): CurrencyLiveResponse {
            return CurrencyLiveResponse(
                privacy = roomResponse.privacy,
                success = roomResponse.success,
                source = roomResponse.source,
                terms = roomResponse.terms,
                timestamp = roomResponse.timestamp,
                currencyRates = Gson().fromJson(roomResponse.currencyRates, JsonObject::class.java)
            )
        }
    }
}