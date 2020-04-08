package com.hafizhnotes.currencyconversion.ui.currency_rates

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.hafizhnotes.currencyconversion.R
import com.hafizhnotes.currencyconversion.data.api.CurrencyLayerClient
import com.hafizhnotes.currencyconversion.data.repository.NetworkState
import com.hafizhnotes.currencyconversion.data.repository.Status
import kotlinx.android.synthetic.main.fragment_current_rates.view.*
import kotlinx.android.synthetic.main.item_error_full_page.view.*

class CurrentRatesFragment(private val source: String = "USD") : Fragment() {
    private lateinit var rootView: View
    private lateinit var repository: CurrencyRatesRepository
    private lateinit var viewModel: CurrencyRatesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView =
            inflater.inflate(R.layout.fragment_current_rates, container, false)

        onBindData()
        return rootView
    }

    private fun onBindData() {
        val apiService = CurrencyLayerClient.getClient()
        repository = CurrencyRatesRepository(apiService)
        viewModel = getViewModel()

        // Init properties of recycler view.
        val layoutManager = LinearLayoutManager(rootView.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rootView.rv_currency_rates.layoutManager = layoutManager

        // Bind the quotes.
        viewModel
            .currencyLive
            .observe(
                this,
                Observer {
                    if (!it.success) return@Observer

                    // Remove the source so the list of currency
                    // doesn't get duplicated.
                    it.currencyRates.remove("$source$source")

                    val adapter = CurrencyRateAdapter(rootView.context, source, it.currencyRates)
                    rootView.rv_currency_rates.adapter = adapter
                }
            )


        // Bind the network state.
        viewModel
            .networkState
            .observe(
                this,
                Observer {
                    rootView.sfl_rates_loading.visibility =
                        if (it == NetworkState.LOADING) View.VISIBLE else View.GONE

                    if (it.status == Status.FAILED) {
                        rootView.ll_error_page.visibility = View.VISIBLE
                        rootView.tv_error_log.text = it.message
                    } else {
                        rootView.ll_error_page.visibility = View.GONE
                        rootView.tv_error_log.text = resources.getString(R.string.log_failed)
                    }
                }
            )
    }

    private fun getViewModel(): CurrencyRatesViewModel =
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return CurrencyRatesViewModel(
                        repository
                    ) as T
                }
            }
        )[CurrencyRatesViewModel::class.java]
}
