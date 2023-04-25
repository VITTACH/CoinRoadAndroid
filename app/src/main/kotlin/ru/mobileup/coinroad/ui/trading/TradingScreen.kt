package ru.mobileup.coinroad.ui.trading

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ScreenTradingBinding
import ru.mobileup.coinroad.ui.base.BaseScreen

class TradingScreen : BaseScreen<TradingViewModel>(R.layout.screen_trading) {

    private val binding by viewBinding(ScreenTradingBinding::bind)
    override val vm: TradingViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() = with(binding) {
    }
}