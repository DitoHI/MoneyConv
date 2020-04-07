package com.hafizhnotes.currencyconversion.ui.currency_rates

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.hafizhnotes.currencyconversion.R
import kotlinx.android.synthetic.main.fragment_current_rates.view.*
import kotlinx.android.synthetic.main.fragment_exchange_currency.view.*

/**
 * A simple [Fragment] subclass.
 */
class CurrentRatesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =
            inflater.inflate(R.layout.fragment_current_rates, container, false)

        onBindStart(rootView)
        return rootView
    }

    private fun onBindStart(view: View) {
        // Testing.
        // Spinner.
        view.sfl_rates_loading.startShimmer()
    }

}
