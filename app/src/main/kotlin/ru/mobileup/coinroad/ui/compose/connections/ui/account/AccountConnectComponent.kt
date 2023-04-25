package ru.mobileup.coinroad.ui.compose.connections.ui.account

import com.arkivanov.decompose.ComponentContext

interface AccountConnectComponent {
    fun onWalletClick()
}

class RealAccountConnectComponent(
    componentContext: ComponentContext,
    val onOpenWallet: () -> Unit
) : ComponentContext by componentContext, AccountConnectComponent {

    override fun onWalletClick() {
        onOpenWallet()
    }
}