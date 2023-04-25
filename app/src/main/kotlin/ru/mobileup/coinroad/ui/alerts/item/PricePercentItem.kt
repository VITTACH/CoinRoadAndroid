package ru.mobileup.coinroad.ui.alerts.item

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ItemPricePercentBinding
import ru.mobileup.coinroad.util.ui.doOnClick
import ru.mobileup.coinroad.util.ui.getColorFromAttr

enum class PricePercent(val value: String) {
    MINUS_FIVE_PERCENT("-5"),
    MINUS_ONE_PERCENT("-1"),
    ONE_PERCENT("+1"),
    FIVE_PERCENT("+5");
}

class PricePercentItem(
    private val value: String,
    private val onPercentClicked: (value: String) -> Unit,
) : BindableItem<ItemPricePercentBinding>() {

    override fun getLayout() = R.layout.item_price_percent

    override fun getId(): Long = value.hashCode().toLong()

    override fun initializeViewBinding(view: View) = ItemPricePercentBinding.bind(view)

    override fun bind(binding: ItemPricePercentBinding, itemPosition: Int) = with(binding.button) {
        when {
            value.contains("-") -> {
                text = "$value%"
                setBackgroundColor(context.getColor(R.color.red_a))
            }
            value.contains("+") -> {
                text = "$value%"
                setBackgroundColor(context.getColor(R.color.green_a))
            }
            else -> {
                text = value
                setBackgroundColor(context.getColorFromAttr(R.attr.secondaryButtonBackgroundColor))
            }
        }
        doOnClick { onPercentClicked.invoke(value) }
    }
}

fun List<String>.toGroupieItems(
    onPercentClicked: (value: String) -> Unit
): List<PricePercentItem> {
    return map { PricePercentItem(it, onPercentClicked) }
}