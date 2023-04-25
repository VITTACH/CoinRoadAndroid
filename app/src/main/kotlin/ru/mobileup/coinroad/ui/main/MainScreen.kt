package ru.mobileup.coinroad.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OVER_SCROLL_NEVER
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ScreenMainBinding
import ru.mobileup.coinroad.ui.base.BaseScreen
import ru.mobileup.coinroad.ui.base.BaseViewModel
import ru.mobileup.coinroad.ui.dashboard.DashboardScreen
import ru.mobileup.coinroad.ui.portfolio.PortfolioScreen
import ru.mobileup.coinroad.ui.trading.TradingScreen
import ru.mobileup.coinroad.util.ui.getTabTextView


class MainScreen : BaseScreen<MainViewModel>(R.layout.screen_main) {

    private enum class Screen {
        Dashboard, Portfolio, Trading
    }

    private val binding by viewBinding(ScreenMainBinding::bind)
    override val vm: MainViewModel by viewModel()

    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        initViews()
        setupSoonCaption()
    }

    override fun onDestroyView() {
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
    }

    private fun initViews() = with(binding) {
        viewPager.adapter = MainScreenAdapter(this@MainScreen)
        (viewPager.getChildAt(0) as RecyclerView).overScrollMode = OVER_SCROLL_NEVER

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (!(position == 0 || position == 1 && positionOffset <= 0)) {
                    viewPager.currentItem = position
                }
            }
        })

        tabLayoutMediator = TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab, position ->
            tab.text = when (position) {
                Screen.Dashboard.ordinal -> getString(R.string.main_dashboard)
                Screen.Portfolio.ordinal -> getString(R.string.main_portfolio)
                Screen.Trading.ordinal -> getString(R.string.main_trading)
                else -> throw IllegalStateException()
            }
        }.apply { attach() }
    }

    private fun setupToolbar() = with(binding) {
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settings -> vm.onSettingsClicked()
                R.id.alerts -> vm.onAlertsClicked()
                else -> return@setOnMenuItemClickListener false
            }
            return@setOnMenuItemClickListener true
        }
    }

    // TODO: add proper soon caption alignment
    private fun setupSoonCaption() = with(binding) {
        val tradingTabTextView = tabLayout.getTabTextView(Screen.Trading.ordinal)
    }

    class MainScreenAdapter(fragment: Fragment) : FragmentStateAdapter(
        fragment.childFragmentManager,
        fragment.viewLifecycleOwner.lifecycle
    ) {
        override fun getItemCount() = Screen.values().size

        override fun createFragment(position: Int): BaseScreen<out BaseViewModel> {
            return when (Screen.values()[position]) {
                Screen.Dashboard -> DashboardScreen()
                Screen.Portfolio -> PortfolioScreen()
                Screen.Trading -> TradingScreen()
            }
        }
    }
}