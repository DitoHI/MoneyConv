package com.hafizhnotes.currencyconversion.ui.currency_rates

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.hafizhnotes.currencyconversion.R

/**
 * A simple [Fragment] subclass.
 */
class CurrentRatesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_rates, container, false)
    }
}
