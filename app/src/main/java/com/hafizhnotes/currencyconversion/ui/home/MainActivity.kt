package com.hafizhnotes.currencyconversion.ui.home

import android.os.Bundle
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hafizhnotes.currencyconversion.R
import com.hafizhnotes.currencyconversion.ui.currency_rates.CurrentRatesFragment
import com.hafizhnotes.currencyconversion.ui.exchange_currency.ExchangeCurrencyFragment

private const val NUM_PAGES = 2

class MainActivity : FragmentActivity() {
    private lateinit var viewPagerHome: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPagerHome = findViewById(R.id.vp2_home)

        val pagerAdapterHome = MainActivityAdapter(this)
        viewPagerHome.adapter = pagerAdapterHome

        // Setup tab.
        val tabHome: TabLayout = findViewById(R.id.tab_home)
        TabLayoutMediator(tabHome, viewPagerHome) { tab, position ->
            when (position) {
                1 -> tab.text = resources.getString(R.string.tab_exchange_currency)
                else -> tab.text = resources.getString(R.string.tab_currency_rates)
            }
        }.attach()
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