package com.hafizhnotes.currencyconversion.ui.exchange_currency

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hafizhnotes.currencyconversion.R
import com.hafizhnotes.currencyconversion.data.api.CurrencyLayerClient
import com.hafizhnotes.currencyconversion.data.constant.TestingConstant
import com.hafizhnotes.currencyconversion.data.repository.NetworkState
import com.hafizhnotes.currencyconversion.data.repository.Status
import com.hafizhnotes.currencyconversion.data.vo.CurrencyListResponse
import kotlinx.android.synthetic.main.fragment_exchange_currency.view.*
import kotlinx.android.synthetic.main.item_error_full_page.view.*
import java.lang.Exception
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class ExchangeCurrencyFragment : Fragment() {
    private lateinit var rootView: View
    private lateinit var repository: ExchangeCurrencyRepository
    private lateinit var viewModel: ExchangeCurrencyViewModel

    private var fromCurrencyDefault = "USD - United States Dollar"
    private val toCurrencyDefault = "JPY - Japanese Yen"

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

        onBindStart()
        onBindDataUI()
    }

    private fun onBindStart() {
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

            val currency = rootView.sp_currency_from.selectedItem.toString().take(3)
            val outputCurrencyFormat =
                formatCurrencyInput(currency, rootView.et_currency_from.text)

            rootView.et_currency_from.setText(outputCurrencyFormat)
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
                }
            )

        // Bind the network state.
        viewModel
            .networkState
            .observe(
                viewLifecycleOwner,
                Observer {
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

    private fun formatCurrencyInput(source: String, input: CharSequence?): String {
        if (input == null || input.isEmpty()) return ""

        var inputStr = input.toString().replace("[^\\d.]", "")
        inputStr = inputStr.replace(",", "")
        if (inputStr.isEmpty()) return ""

        // Convert inputted text to double.
        val inputDouble = inputStr.toDouble()

        val format = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 0

        // Remove the symbol of currency.
        val decimalSymbols = (format as DecimalFormat).decimalFormatSymbols
        decimalSymbols.currencySymbol = ""

        return try {
            format.currency = Currency.getInstance(source.toUpperCase(Locale.ROOT))
            format.decimalFormatSymbols = decimalSymbols

            format.format(inputDouble)
        } catch (e: Exception) {
            format.currency = Currency.getInstance(Locale.US)
            format.decimalFormatSymbols = decimalSymbols

            format.format(inputDouble)
        }
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
}
