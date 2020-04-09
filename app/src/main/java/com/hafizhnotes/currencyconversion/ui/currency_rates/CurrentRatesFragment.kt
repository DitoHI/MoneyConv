package com.hafizhnotes.currencyconversion.ui.currency_rates

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hafizhnotes.currencyconversion.R
import com.hafizhnotes.currencyconversion.data.api.CurrencyLayerClient
import com.hafizhnotes.currencyconversion.data.constant.TestingConstant
import com.hafizhnotes.currencyconversion.data.helper.DateTimeHelper
import com.hafizhnotes.currencyconversion.data.repository.NetworkState
import com.hafizhnotes.currencyconversion.data.repository.Status
import com.hafizhnotes.currencyconversion.data.vo.CurrencyLiveResponse
import kotlinx.android.synthetic.main.fragment_current_rates.view.*
import kotlinx.android.synthetic.main.fragment_exchange_currency.view.*
import kotlinx.android.synthetic.main.item_error_full_page.view.*
import java.util.concurrent.TimeUnit


class CurrentRatesFragment(private val source: String = "USD") : Fragment() {
    private lateinit var rootView: View
    private lateinit var repository: CurrencyRatesRepository
    private lateinit var viewModel: CurrencyRatesViewModel
    private lateinit var roomViewModel: CurrencyRatesRoomViewModel
    private var isUpdatedViaApi = false
    private var isUpdatedViaLocal = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView =
            inflater.inflate(R.layout.fragment_current_rates, container, false)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        onBindRoomData()
        onBindData()
    }

    private fun onBindRoomData() {

        // Get local currency live using Room.
        roomViewModel = getRoomViewModel()
        roomViewModel.latestCurrencyLive.observe(
            viewLifecycleOwner,
            Observer {
                if (isUpdatedViaApi) return@Observer

                if (it.isEmpty()) return@Observer

                // Check the timestamp
                // If more than 30 minutes, then fetch from API.
                val liveResponse = it.first()
                val timestampNow =
                    DateTimeHelper
                        .unixToDate(System.currentTimeMillis() / 1000L)

                val timestampFetched =
                    DateTimeHelper
                        .unixToDate(liveResponse.timestamp.toLong())

                val diff =
                    TimeUnit.MINUTES.convert(
                        timestampNow.time - timestampFetched.time,
                        TimeUnit.MILLISECONDS
                    )
                if (diff > 30) return@Observer

                // Bind the data from local
                isUpdatedViaLocal = true
                rootView.sfl_rates_loading.visibility = View.GONE
                Log.d(TestingConstant.TAG_BIND_VIA, "Local")
                onBindCurrencyLive(CurrencyLiveResponse.fromRoomResponse(liveResponse))
            }
        )
    }

    private fun onBindData() {
        val apiService = CurrencyLayerClient.getClient()
        repository = CurrencyRatesRepository(apiService)
        viewModel = getViewModel()

        // Bind the rates.
        viewModel
            .currencyLive
            .observe(
                viewLifecycleOwner,
                Observer {
                    if (!it.success) return@Observer

                    if (isUpdatedViaLocal) return@Observer

                    Log.d(TestingConstant.TAG_BIND_VIA, "API")
                    onBindCurrencyLive(it)
                    isUpdatedViaApi = true

                    // Insert to local if possible
                    if (::roomViewModel.isInitialized) {
                        roomViewModel.insert(it.toRoomResponse())
                    }
                }
            )


        // Bind the network state.
        viewModel
            .networkState
            .observe(
                viewLifecycleOwner,
                Observer {
                    if (isUpdatedViaLocal) return@Observer

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

    private fun onBindCurrencyLive(it: CurrencyLiveResponse) {
        // Init properties of recycler view.
        val layoutManager = LinearLayoutManager(rootView.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rootView.rv_currency_rates.layoutManager = layoutManager

        // Remove the source so the list of currency
        // doesn't get duplicated.
        it.currencyRates.remove("$source$source")

        val adapter = CurrencyRateAdapter(rootView.context, source, it.currencyRates)
        rootView.rv_currency_rates.adapter = adapter
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

    private fun getRoomViewModel(): CurrencyRatesRoomViewModel =
        ViewModelProvider(this).get(CurrencyRatesRoomViewModel::class.java)
}
