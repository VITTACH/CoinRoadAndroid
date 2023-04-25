package ru.mobileup.coinroad.ui.compose.portfolio.ui.account

import com.arkivanov.decompose.ComponentContext

interface AccountPortfolioComponent

class RealAccountPortfolioComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, AccountPortfolioComponent