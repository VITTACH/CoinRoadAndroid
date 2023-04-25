package ru.mobileup.coinroad.ui.compose.portfolio.ui.wallet

import com.arkivanov.decompose.ComponentContext

interface WalletPortfolioComponent

class RealWalletPortfolioComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, WalletPortfolioComponent