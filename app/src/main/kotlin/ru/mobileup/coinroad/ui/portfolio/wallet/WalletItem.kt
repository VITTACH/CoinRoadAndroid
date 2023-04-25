package ru.mobileup.coinroad.ui.portfolio.wallet

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ItemWalletBinding
import ru.mobileup.coinroad.domain.common.Connection

class WalletItem(
    private val wallet: Connection.Wallet,
    private val onWalletClicked: (wallet: Connection.Wallet) -> Unit
) : BindableItem<ItemWalletBinding>() {

    override fun getLayout() = R.layout.item_wallet

    override fun initializeViewBinding(view: View) = ItemWalletBinding.bind(view)

    override fun bind(binding: ItemWalletBinding, position: Int) {
    }
}