package ru.mobileup.coinroad.ui.compose.connections

import com.arkivanov.decompose.ComponentContext
import org.koin.dsl.module
import ru.mobileup.coinroad.ui.compose.connections.ui.account.AccountConnectComponent
import ru.mobileup.coinroad.ui.compose.connections.ui.account.RealAccountConnectComponent
import ru.mobileup.coinroad.ui.compose.connections.ui.wallet.RealWalletConnectComponent
import ru.mobileup.coinroad.ui.compose.connections.ui.wallet.WalletConnectComponent
import ru.mobileup.coinroad.ui.compose.core.ui.ComponentFactory

val connectionModule = module {
}

fun ComponentFactory.createAccountConnectComponent(
    componentContext: ComponentContext,
    onOpenWallet: () -> Unit
): AccountConnectComponent {
    return RealAccountConnectComponent(componentContext, onOpenWallet)
}

fun ComponentFactory.createWalletConnectComponent(
    componentContext: ComponentContext
): WalletConnectComponent {
    return RealWalletConnectComponent(componentContext)
}