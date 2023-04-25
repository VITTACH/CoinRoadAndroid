package ru.mobileup.coinroad.ui.compose.connections.ui.wallet

import com.arkivanov.decompose.ComponentContext

interface WalletConnectComponent

class RealWalletConnectComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, WalletConnectComponent