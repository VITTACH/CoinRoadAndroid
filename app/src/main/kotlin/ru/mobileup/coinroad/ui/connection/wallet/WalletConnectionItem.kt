package ru.mobileup.coinroad.ui.connection.wallet

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ItemWalletConnectionBinding
import ru.mobileup.coinroad.domain.common.Connection

class WalletConnectionItem(
    private val wallet: Connection.Wallet,
    private val onWalletClicked: (wallet: Connection.Wallet) -> Unit
) : BindableItem<ItemWalletConnectionBinding>() {

    override fun getLayout() = R.layout.item_wallet_connection

    override fun initializeViewBinding(view: View) = ItemWalletConnectionBinding.bind(view)

    override fun bind(binding: ItemWalletConnectionBinding, position: Int) {
    }
}
