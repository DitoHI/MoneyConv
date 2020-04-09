package com.hafizhnotes.currencyconversion.ui.currency_rates

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import com.hafizhnotes.currencyconversion.R
import com.hafizhnotes.currencyconversion.data.constant.URLConstant
import com.hafizhnotes.currencyconversion.data.vo.CurrencyLiveResponse
import kotlinx.android.synthetic.main.item_currency_rates.view.*
import java.text.NumberFormat
import java.util.*

class CurrencyRateAdapter(
    private val context: Context,
    private val source: String,
    private val liveResponse: CurrencyLiveResponse,
    private val sourceSum: Double = 1.0
) : RecyclerView.Adapter<CurrencyRateAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater
                .from(context)
                .inflate(R.layout.item_currency_rates, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return liveResponse.currencyRates.keySet().size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ratesJsonObject = liveResponse.currencyRates
        var currencyNames = ratesJsonObject.keySet().toList()

        @SuppressLint("SetTextI18n")
        fun setData(position: Int) {
            // Get destination currency.
            val currencyName = currencyNames[position]
            val destinationCurrency =
                currencyName.replace("USD", "", true)

            val rate = liveResponse.convertCurrency(source, destinationCurrency, sourceSum)

            // If the holder[position] is the source, then hide the view.
            if (destinationCurrency.isEmpty()) return

            // Get url img for the destination.
            val destinationCountry =
                destinationCurrency
                    .substring(0, destinationCurrency.length - 1)
                    .toLowerCase(Locale.ROOT)

            /**
             * Bind the UI
             */
            // Get the image url of country flags.
            val glideOptions =
                RequestOptions()
                    .centerInside()
                    .error(R.drawable.ic_globe)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)

            Glide
                .with(itemView)
                .load(URLConstant.countryFlagUri(destinationCountry))
                .apply(glideOptions)
                .into(itemView.iv_country_flag)

            // Format currency from.
            val fromFormat = NumberFormat.getCurrencyInstance()
            fromFormat.maximumFractionDigits = 0
            fromFormat.currency = Currency.getInstance(source.toUpperCase(Locale.ROOT))

            // Format currency to.
            val toFormat = NumberFormat.getCurrencyInstance()
            toFormat.maximumFractionDigits = 0
            toFormat.currency = Currency.getInstance(destinationCurrency.toUpperCase(Locale.ROOT))

            // Currency Result
            val fromCurrencyAmount = fromFormat.format(sourceSum)
            val toCurrencyAmount = toFormat.format(rate)

            itemView.tv_rate_currency_from.text = source.toUpperCase(Locale.ROOT)
            itemView.tv_rate_currency_from_sum.text = fromCurrencyAmount
            itemView.tv_rate_currency_to.text = destinationCountry.toUpperCase(Locale.ROOT)
            itemView.tv_rate_currency_to_sum.text = toCurrencyAmount
            itemView.tv_rates_result.text = toCurrencyAmount
        }
    }
}