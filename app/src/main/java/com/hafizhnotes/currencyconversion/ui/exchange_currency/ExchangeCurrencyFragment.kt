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
import com.hafizhnotes.currencyconversion.R
import kotlinx.android.synthetic.main.fragment_exchange_currency.view.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class ExchangeCurrencyFragment : Fragment() {
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView =
            inflater.inflate(R.layout.fragment_exchange_currency, container, false)

        onBindStart()
        return rootView
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

            val currency = rootView.sp_currency_from.selectedItem.toString()
            val outputCurrencyFormat =
                formatCurrencyInput(currency, rootView.et_currency_from.text)

            rootView.et_currency_from.setText(outputCurrencyFormat)
        }

        // Remove focus when enter is clicked.
        rootView.et_currency_from.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideSoftKeyboard(activity!!)
                rootView.et_currency_from.clearFocus()
                return@OnEditorActionListener true
            }
            false
        })

        // Testing.
        // Skeleton.
//        view.sfl_exchange_loading.visibility = View.VISIBLE
//        view.sfl_exchange_loading.startShimmer()

        // Testing.
        // Spinner.
        val fromCurrencyAdapter =
            ArrayAdapter(
                rootView.context,
                R.layout.spinner_common_text,
                listOf("USD", "IDR")
            )

        fromCurrencyAdapter.setDropDownViewResource(R.layout.spinner_common_dropdown)
        rootView.sp_currency_from.adapter = fromCurrencyAdapter
        val toCurrencyAdapter =
            ArrayAdapter(
                rootView.context,
                R.layout.spinner_common_text,
                listOf("USD", "IDR")
            )

        toCurrencyAdapter.setDropDownViewResource(R.layout.spinner_common_dropdown)
        rootView.sp_currency_to.adapter = toCurrencyAdapter
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
        format.currency = Currency.getInstance(source.toUpperCase(Locale.ROOT))

        // Remove the symbol of currency.
        val decimalSymbols = (format as DecimalFormat).decimalFormatSymbols
        decimalSymbols.currencySymbol = ""
        format.decimalFormatSymbols = decimalSymbols

        return format.format(inputDouble)
    }

    private fun setupUI(view: View) {
        if (activity == null) return

        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
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
}
