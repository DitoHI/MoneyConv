package com.hafizhnotes.currencyconversion.ui.currency_rates

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hafizhnotes.currencyconversion.R
import com.hafizhnotes.currencyconversion.data.api.CurrencyLayerClient
import com.hafizhnotes.currencyconversion.data.constant.ResourceConstant
import com.hafizhnotes.currencyconversion.data.constant.TestingConstant
import com.hafizhnotes.currencyconversion.data.repository.NetworkState
import com.hafizhnotes.currencyconversion.data.repository.Status
import com.hafizhnotes.currencyconversion.data.vo.CurrencyLiveResponse
import com.hafizhnotes.currencyconversion.ui.home.MainActivity
import kotlinx.android.synthetic.main.fragment_current_rates.view.*
import kotlinx.android.synthetic.main.item_error_full_page.view.*
import java.util.*
import java.util.concurrent.TimeUnit


class CurrentRatesFragment : Fragment() {
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

        // Get currency live in local db using Room.
        roomViewModel = getRoomViewModel()
        roomViewModel.latestCurrencyLive.observe(
            viewLifecycleOwner,
            Observer {
                if (isUpdatedViaApi) return@Observer

                if (it.isEmpty()) return@Observer

                // Check the timestamp
                // If more than 30 minutes, then fetch from API.
                val liveResponse = it.first()
                val timeZone = TimeZone.getTimeZone("IST")
                val dateNow = Calendar.getInstance(timeZone).time
                val diff = dateNow.time - liveResponse.createdAt.time
                val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff)

                if (diffInMinutes >= 30) return@Observer

                // Bind the data from local
                isUpdatedViaLocal = true

                // Updated related views.
                rootView.sfl_rates_loading.visibility = View.GONE
                rootView.ll_error_page.visibility = View.GONE
                rootView.tv_error_log.text = resources.getString(R.string.log_failed)

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

                    // Insert to local if possible.
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
        // Init arguments
        val mainActivity = activity!! as MainActivity
        val source =
            mainActivity.bundle.getString(ResourceConstant.BUNDLE_KEY_SOURCE, "USD")

        val sourceValue =
            mainActivity.bundle.getDouble(ResourceConstant.BUNDLE_KEY_SOURCE_VALUE, 1.0)

        // Init properties of recycler view.
        val layoutManager = LinearLayoutManager(rootView.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rootView.rv_currency_rates.layoutManager = layoutManager

        // Remove the source so the list of currency
        // doesn't get duplicated.
        it.currencyRates.remove("$source$source")

        val adapter =
            CurrencyRateAdapter(
                context = rootView.context,
                source = source,
                sourceSum = sourceValue,
                liveResponse = it
            )

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
