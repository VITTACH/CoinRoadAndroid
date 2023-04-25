package ru.mobileup.coinroad.ui.exchange

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import me.aartikov.sesame.loading.simple.Loading
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ScreenExchangeChoosingBinding
import ru.mobileup.coinroad.ui.base.BaseScreen
import ru.mobileup.coinroad.util.ui.doOnClick

class ExchangeChoosingScreen :
    BaseScreen<ExchangeChoosingViewModel>(R.layout.screen_exchange_choosing) {

    private val binding by viewBinding(ScreenExchangeChoosingBinding::bind)
    override val vm: ExchangeChoosingViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() = with(binding) {
        val buttons = listOf(firstExchangeBtn, secondExchangeBtn, thirdExchangeBtn)

        aboutProject.doOnClick { vm.onAboutClicked() }

        moreExchangeButton.doOnClick { vm.onMoreExchangesClicked() }

        vm::exchangesState bind { state ->
            if (state is Loading.State.Data) {
                buttons.forEachIndexed { index, button ->
                    val exchange = state.data[index]
                    button.text = exchange.name
                    button.doOnClick { vm.onDialogExchangeClicked(exchange) }
                }
            }
        }
    }
}