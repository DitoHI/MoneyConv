package com.hafizhnotes.currencyconversion.ui.exchange_currency

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.hafizhnotes.currencyconversion.R
import kotlinx.android.synthetic.main.fragment_exchange_currency.view.*

/**
 * A simple [Fragment] subclass.
 */
class ExchangeCurrencyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =
            inflater.inflate(R.layout.fragment_exchange_currency, container, false)

        onBindStart(rootView)
        return rootView
    }

    private fun onBindStart(view: View) {
        // Exit if activity is null.
        if (activity == null) return

        // Testing.
        // Spinner.
        val fromCurrencyAdapter =
            ArrayAdapter(
                activity!!.applicationContext,
                R.layout.spinner_common_text,
                listOf("USD", "IDR")
            )

        fromCurrencyAdapter.setDropDownViewResource(R.layout.spinner_common_dropdown)
        view.sp_currency_from.adapter = fromCurrencyAdapter


        val toCurrencyAdapter =
            ArrayAdapter(
                activity!!.applicationContext,
                R.layout.spinner_common_text,
                listOf("USD", "IDR")
            )

        toCurrencyAdapter.setDropDownViewResource(R.layout.spinner_common_dropdown)
        view.sp_currency_to.adapter = toCurrencyAdapter
    }
}
