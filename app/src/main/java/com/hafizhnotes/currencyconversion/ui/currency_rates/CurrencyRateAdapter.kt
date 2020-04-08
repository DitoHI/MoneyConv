package com.hafizhnotes.currencyconversion.ui.currency_rates

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.hafizhnotes.currencyconversion.R
import com.hafizhnotes.currencyconversion.data.constant.URLConstant
import kotlinx.android.synthetic.main.fragment_exchange_currency.view.*
import kotlinx.android.synthetic.main.item_currency_rates.view.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.reflect.KProperty1

class CurrencyRateAdapter(
    val context: Context,
    private val source: String,
    private val quotesJSONObject: JsonObject,
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
        return quotesJSONObject.keySet().size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var quoteCurrencies = quotesJSONObject.keySet().toList()

        @SuppressLint("SetTextI18n")
        fun setData(position: Int) {
            // Get destination currency.
            val quoteName = quoteCurrencies[position]
            val rate = quotesJSONObject[quoteName].asDouble
            val destinationCurrency = quoteName.replace(source, "", true)

            // Get url img for the destination.
            val destinationCountry =
                destinationCurrency
                    .substring(0, destinationCurrency.length - 1)
                    .toLowerCase(Locale.ROOT)

            // Bind the UI.
            Glide
                .with(itemView)
                .load(URLConstant.countryFlagUri(destinationCountry))
                .into(itemView.iv_country_flag)

            itemView.tv_rate_currency_from.text = source.toUpperCase(Locale.ROOT)
            itemView.tv_rate_currency_from_sum.text = "1 $source"
            itemView.tv_rate_currency_to.text = destinationCountry.toUpperCase(Locale.ROOT)
            itemView.tv_rate_currency_to_sum.text = "%.2f".format(rate)

            // Format currency.
            val custom = DecimalFormatSymbols()
            custom.decimalSeparator = '.'
            custom.groupingSeparator = ','
            val format = DecimalFormat.getInstance() as DecimalFormat
            format.decimalFormatSymbols = custom
            format.currency = Currency.getInstance(destinationCurrency)
            itemView.tv_rates_result.text = format.format(rate)
        }
    }
}