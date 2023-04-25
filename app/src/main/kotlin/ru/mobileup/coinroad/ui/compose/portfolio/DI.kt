package ru.mobileup.coinroad.ui.compose.portfolio

import com.arkivanov.decompose.ComponentContext
import org.koin.dsl.module
import ru.mobileup.coinroad.ui.compose.core.ui.ComponentFactory
import ru.mobileup.coinroad.ui.compose.portfolio.ui.account.AccountPortfolioComponent
import ru.mobileup.coinroad.ui.compose.portfolio.ui.account.RealAccountPortfolioComponent
import ru.mobileup.coinroad.ui.compose.portfolio.ui.wallet.RealWalletPortfolioComponent
import ru.mobileup.coinroad.ui.compose.portfolio.ui.wallet.WalletPortfolioComponent

val portfolioModule = module {
}

fun ComponentFactory.createAccountPortfolioComponent(
    componentContext: ComponentContext
): AccountPortfolioComponent {
    return RealAccountPortfolioComponent(componentContext)
}

fun ComponentFactory.createWalletPortfolioComponent(
    componentContext: ComponentContext
): WalletPortfolioComponent {
    return RealWalletPortfolioComponent(componentContext)
}