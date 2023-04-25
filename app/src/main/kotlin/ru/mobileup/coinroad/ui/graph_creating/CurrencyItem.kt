package ru.mobileup.coinroad.ui.graph_creating

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ItemCurrencyBinding
import ru.mobileup.coinroad.domain.common.Currency
import ru.mobileup.coinroad.util.ui.doOnClick

class CurrencyItem(
    private val currency: Currency,
    private val code: String?,
    private val onCurrencyClicked: (currency: Currency) -> Unit,
) : BindableItem<ItemCurrencyBinding>() {

    override fun getLayout() = R.layout.item_currency

    override fun getId(): Long = currency.hashCode().toLong()

    override fun initializeViewBinding(view: View) = ItemCurrencyBinding.bind(view)

    override fun bind(viewBinding: ItemCurrencyBinding, position: Int) = with(viewBinding) {
        currencyName.text = currency.name
        currencyCode.text = code ?: ""
        root.doOnClick { onCurrencyClicked.invoke(currency) }
    }
}

fun List<Currency>.toGroupieItems(
    onCurrencyClicked: (currency: Currency) -> Unit
): List<CurrencyItem> {
    var oldCode = ""
    return this.map {
        val curCode = it.id.first().toString()
        val code = if (curCode != oldCode) {
            oldCode = curCode
            oldCode
        } else null
        CurrencyItem(it, code, onCurrencyClicked)
    }
}