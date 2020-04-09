package com.hafizhnotes.currencyconversion.ui.home

import android.os.Bundle
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hafizhnotes.currencyconversion.R
import com.hafizhnotes.currencyconversion.data.helper.DateTimeHelper
import com.hafizhnotes.currencyconversion.ui.currency_rates.CurrentRatesFragment
import com.hafizhnotes.currencyconversion.ui.exchange_currency.ExchangeCurrencyFragment
import com.mikepenz.iconics.Iconics
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.TimeUnit

private const val NUM_PAGES = 2

class MainActivity : FragmentActivity() {
    private lateinit var viewPagerHome: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup icon.
        Iconics.init(applicationContext)

        // Setup pager.
        viewPagerHome = findViewById(R.id.vp2_home)
        val pagerAdapterHome = MainActivityAdapter(this)
        viewPagerHome.adapter = pagerAdapterHome

        // Setup tab.
        val tabHome: TabLayout = findViewById(R.id.tab_home)
        TabLayoutMediator(tabHome, viewPagerHome) { tab, position ->
            when (position) {
                1 -> tab.text = resources.getString(R.string.tab_currency_rates)
                else -> tab.text = resources.getString(R.string.tab_exchange_currency)
            }
        }.attach()

        val now = DateTimeHelper.unixToDate(System.currentTimeMillis() / 1000L)
        val fetch = DateTimeHelper.unixToDate(1586363706L)
        val diff = now.time - fetch.time
        val minutes = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS)
        print(now)
    }

    private inner class MainActivityAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                1 -> CurrentRatesFragment()
                else -> ExchangeCurrencyFragment()
            }
        }
    }
}