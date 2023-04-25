package ru.mobileup.coinroad.ui.exchange

import android.view.View
import androidx.core.view.isVisible
import com.xwray.groupie.viewbinding.BindableItem
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ItemExchangeBinding
import ru.mobileup.coinroad.domain.common.Exchange
import ru.mobileup.coinroad.util.iconResource
import ru.mobileup.coinroad.util.ui.doOnClick

class ExchangeItem(
    private val exchange: Exchange,
    private val onRegistrationClicked: (exchange: Exchange) -> Unit,
    private val onExchangeClicked: (exchange: Exchange) -> Unit
) : BindableItem<ItemExchangeBinding>() {

    override fun initializeViewBinding(views: View) = ItemExchangeBinding.bind(views)

    override fun getLayout() = R.layout.item_exchange

    override fun bind(binding: ItemExchangeBinding, position: Int) = with(binding) {
        val isEnabled = exchange.isPublicEnabled
        root.isClickable = isEnabled
        root.isFocusable = isEnabled
        soonCaption.isVisible = !isEnabled
        icon.isVisible = isEnabled

        with(exchangeIcon) {
            setImageResource(exchange.iconResource)
            if (isEnabled) {
                root.doOnClick { onExchangeClicked(exchange) }
                setBackgroundResource(R.drawable.background_circle)
            } else background = null
            doOnClick { onRegistrationClicked(exchange) }
        }
        exchangeName.text = exchange.name
    }
}

fun List<Exchange>.toGroupieItems(
    onRegistrationClicked: (exchange: Exchange) -> Unit,
    onExchangeClicked: (exchange: Exchange) -> Unit
) = map { ExchangeItem(it, onRegistrationClicked, onExchangeClicked) }
