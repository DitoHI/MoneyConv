package com.hafizhnotes.currencyconversion.ui.exchange_currency

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hafizhnotes.currencyconversion.R
import com.hafizhnotes.currencyconversion.data.api.CurrencyLayerClient
import com.hafizhnotes.currencyconversion.data.constant.ResourceConstant
import com.hafizhnotes.currencyconversion.data.constant.TestingConstant
import com.hafizhnotes.currencyconversion.data.repository.NetworkState
import com.hafizhnotes.currencyconversion.data.repository.Status
import com.hafizhnotes.currencyconversion.data.vo.CurrencyListResponse
import com.hafizhnotes.currencyconversion.data.vo.CurrencyLiveResponse
import com.hafizhnotes.currencyconversion.ui.currency_rates.CurrencyRatesRoomViewModel
import com.hafizhnotes.currencyconversion.ui.home.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_exchange_currency.*
import kotlinx.android.synthetic.main.fragment_exchange_currency.view.*
import kotlinx.android.synthetic.main.fragment_exchange_currency.view.ibtn_swap_currency
import kotlinx.android.synthetic.main.item_error_full_page.view.*
import java.lang.Exception
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class ExchangeCurrencyFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var repository: ExchangeCurrencyRepository
    private lateinit var viewModel: ExchangeCurrencyViewModel
    private lateinit var roomViewModel: ExchangeCurrencyRoomViewModel
    private lateinit var rateRoomViewModel: CurrencyRatesRoomViewModel

    private var fromCurrencyDefault = "SGD - Singapore Dollar"
    private val toCurrencyDefault = "IDR - Indonesian Rupiah"

    private var isUpdatedViaApi = false
    private var isUpdatedViaLocal = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        rootView =
            inflater.inflate(R.layout.fragment_exchange_currency, container, false)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        onBindRateRoomDataUI()
        onBindRoomDataUI()
        onBindDataUI()
    }

    private fun onBindRateRoomDataUI() {
        if (activity == null) return

        // Get list of current rates in local db using Room.
        // If not exist then redirected to ExchangeRatesFragment.
        rateRoomViewModel = getRateRoomViewModel()
        rateRoomViewModel.latestCurrencyLive.observe(
            viewLifecycleOwner,
            Observer {
                if (it.isEmpty()) {
                    redirectedToCurrentRate(activity!!)
                    return@Observer
                }

                val listResp = it.first()
                if (!listResp.success) {
                    redirectedToCurrentRate(activity!!)
                    return@Observer
                }

                // Now bind the current rates to
                // textField.
                onBindStart(CurrencyLiveResponse.fromRoomResponse(listResp))
            }
        )
    }


    private fun onBindStart(liveResponse: CurrencyLiveResponse) {

        // Setup UI if screen clicked
        // then remove the focus of text field.
        setupUI(rootView)

        // Disable input text for currency to.
        rootView.et_currency_to.isEnabled = false

        // Automatically format the text
        // based on selected currency
        // for currency from.
        rootView.et_currency_from.setOnFocusChangeListener { _, b ->
            if (b) return@setOnFocusChangeListener

            convertCurrencyBind(liveResponse)
        }

        // Remove focus when enter is clicked.
        rootView
            .et_currency_from
            .setOnEditorActionListener(
                OnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        hideSoftKeyboard(activity!!)
                        rootView.et_currency_from.clearFocus()
                        return@OnEditorActionListener true
                    }
                    false
                })


        // Do the converting each time
        // a new currency is selected.
        val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                convertCurrencyBind(liveResponse)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        rootView.sp_currency_from.onItemSelectedListener = itemSelectedListener
        rootView.sp_currency_to.onItemSelectedListener = itemSelectedListener

        // Clicked btn_convert will be redirected to CurrentRateFragment
        rootView.btn_convert_currency.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.bundle.putString(
                ResourceConstant.BUNDLE_KEY_SOURCE,
                rootView.sp_currency_from.selectedItem.toString().take(3)
            )

            var sourceValue =
                (rootView.et_currency_from.text as CharSequence).toString().toCurrencyDouble()
            sourceValue = if (sourceValue <= 0.0) 1.0 else sourceValue

            mainActivity.bundle.putDouble(
                ResourceConstant.BUNDLE_KEY_SOURCE_VALUE,
                sourceValue
            )

            // Redirect to current rates
            val tabHost = mainActivity.tab_home.getTabAt(1) ?: return@setOnClickListener
            tabHost.select()

            // Notify changed
            mainActivity.viewPagerHome.adapter?.notifyItemChanged(1)
        }
    }

    private fun onBindRoomDataUI() {

        // Get list of currency in local db using Room.
        roomViewModel = getRoomViewModel()
        roomViewModel.allCurrencyLive.observe(
            viewLifecycleOwner,
            Observer {
                if (isUpdatedViaApi) return@Observer

                if (it.isEmpty()) return@Observer

                val listResponse = it.first()
                isUpdatedViaLocal = true

                // Update related view.
                rootView.sfl_exchange_loading.visibility = View.GONE
                rootView.ll_error_page.visibility = View.GONE
                rootView.tv_error_log.text = resources.getString(R.string.log_failed)

                Log.d(TestingConstant.TAG_BIND_VIA, "Local")
                onBindUI(CurrencyListResponse.fromRoomResponse(listResponse))
            }
        )
    }

    private fun onBindDataUI() {
        val apiService = CurrencyLayerClient.getClient()
        repository = ExchangeCurrencyRepository(apiService)
        viewModel = getViewModel()

        // Bind the currency list.
        viewModel
            .currencyList
            .observe(
                viewLifecycleOwner,
                Observer {
                    if (!it.success) return@Observer

                    if (isUpdatedViaLocal) return@Observer

                    Log.d(TestingConstant.TAG_BIND_VIA, "API")
                    onBindUI(it)
                    isUpdatedViaApi = true

                    // Insert to local.
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

                    rootView.sfl_exchange_loading.visibility =
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

    private fun onBindUI(it: CurrencyListResponse) {
        val currencyLabels =
            it.currencies.keySet().map { currency ->
                val name = it
                    .currencies
                    .get(currency)
                    .toString()
                    .replace("\"", "")

                "$currency - $name"
            }

        val fromCurrencyAdapter =
            ArrayAdapter(
                rootView.context,
                R.layout.spinner_common_text,
                currencyLabels
            )

        fromCurrencyAdapter.setDropDownViewResource(R.layout.spinner_common_dropdown)
        rootView.sp_currency_from.adapter = fromCurrencyAdapter
        rootView
            .sp_currency_from
            .setSelection(currencyLabels.indexOf(fromCurrencyDefault))

        val toCurrencyAdapter =
            ArrayAdapter(
                rootView.context,
                R.layout.spinner_common_text,
                currencyLabels
            )

        toCurrencyAdapter.setDropDownViewResource(R.layout.spinner_common_dropdown)
        rootView.sp_currency_to.adapter = toCurrencyAdapter
        rootView
            .sp_currency_to
            .setSelection(currencyLabels.indexOf(toCurrencyDefault))

        // Swap the currency.
        rootView.ibtn_swap_currency.setOnClickListener {
            val currencyFrom = rootView.sp_currency_from.selectedItem.toString()
            val currencyFromValue = rootView.et_currency_from.text
            val currencyTo = rootView.sp_currency_to.selectedItem.toString()
            val currencyToValue = rootView.et_currency_to.text

            rootView.sp_currency_from.setSelection(currencyLabels.indexOf(currencyTo))
            rootView.et_currency_from.text = currencyToValue
            rootView.sp_currency_to.setSelection(currencyLabels.indexOf(currencyFrom))
            rootView.et_currency_to.text = currencyFromValue
        }
    }

    private fun convertCurrencyBind(liveResponse: CurrencyLiveResponse) {
        val currency = rootView.sp_currency_from.selectedItem.toString().take(3)
        val fromValue =
            (rootView.et_currency_from.text as CharSequence).toString().toCurrencyDouble()

        val fromCurrencyFormat = formatCurrencyInput(currency, fromValue)

        rootView.et_currency_from.setText(fromCurrencyFormat)

        // Calculate the converted.
        val toCurrency = rootView.sp_currency_to.selectedItem.toString().take(3)
        val result = liveResponse.convertCurrency(
            currency,
            toCurrency,
            fromValue
        )
        val toCurrencyFormat = formatCurrencyInput(currency, result)
        rootView.et_currency_to.setText(toCurrencyFormat)
    }

    private fun formatCurrencyInput(source: String, input: Double): String {
        val format = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 0

        // Remove the symbol of currency.
        val decimalSymbols = (format as DecimalFormat).decimalFormatSymbols
        decimalSymbols.currencySymbol = ""

        return try {
            format.currency = Currency.getInstance(source.toUpperCase(Locale.ROOT))
            format.decimalFormatSymbols = decimalSymbols

            format.format(input)
        } catch (e: Exception) {
            format.currency = Currency.getInstance(Locale.US)
            format.decimalFormatSymbols = decimalSymbols

            format.format(input)
        }
    }

    private fun String.toCurrencyDouble(): Double {
        if (this.isEmpty()) return 0.0

        var inputStr = this.replace("[^\\d.]", "")
        inputStr = inputStr.replace(",", "")
        if (inputStr.isEmpty()) return 0.0

        return inputStr.toDouble()
    }

    private fun setupUI(view: View) {
        if (activity == null) return

        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideSoftKeyboard(activity!!)
                false
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    private fun hideSoftKeyboard(activity: Activity) {
        if (activity.currentFocus == null) return

        val inputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus!!.windowToken, 0
        )
    }

    private fun getViewModel(): ExchangeCurrencyViewModel =
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return ExchangeCurrencyViewModel(
                        repository
                    ) as T
                }
            }
        )[ExchangeCurrencyViewModel::class.java]

    private fun getRoomViewModel(): ExchangeCurrencyRoomViewModel =
        ViewModelProvider(this).get(ExchangeCurrencyRoomViewModel::class.java)

    private fun getRateRoomViewModel(): CurrencyRatesRoomViewModel =
        ViewModelProvider(this).get(CurrencyRatesRoomViewModel::class.java)

    private fun redirectedToCurrentRate(activity: FragmentActivity) {
        val tabHost = activity.tab_home.getTabAt(1) ?: return
        tabHost.select()
    }
}
