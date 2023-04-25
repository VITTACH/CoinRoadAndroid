package ru.mobileup.coinroad.ui.compose.main.ui

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.RouterState
import com.arkivanov.decompose.router.push
import com.arkivanov.decompose.router.router
import kotlinx.parcelize.Parcelize
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesame.localizedstring.ResourceString
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.ui.compose.connections.createAccountConnectComponent
import ru.mobileup.coinroad.ui.compose.connections.createWalletConnectComponent
import ru.mobileup.coinroad.ui.compose.connections.ui.account.AccountConnectComponent
import ru.mobileup.coinroad.ui.compose.connections.ui.wallet.WalletConnectComponent
import ru.mobileup.coinroad.ui.compose.core.ui.ComponentFactory
import ru.mobileup.coinroad.ui.compose.core.ui.utils.toComposeState
import ru.mobileup.coinroad.ui.compose.portfolio.createAccountPortfolioComponent
import ru.mobileup.coinroad.ui.compose.portfolio.createWalletPortfolioComponent
import ru.mobileup.coinroad.ui.compose.portfolio.ui.account.AccountPortfolioComponent
import ru.mobileup.coinroad.ui.compose.portfolio.ui.wallet.WalletPortfolioComponent

interface MainComponent {
    val state: RouterState<*, Child>

    val title: LocalizedString

    sealed interface Child {
        class AccountConnect(val component: AccountConnectComponent) : Child
        class WalletConnect(val component: WalletConnectComponent) : Child
        class AccountPortfolio(val component: AccountPortfolioComponent) : Child
        class WalletPortfolio(val component: WalletPortfolioComponent) : Child
    }
}

class RealMainComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory
) : ComponentContext by componentContext, MainComponent {

    private val router = router<ChildConfig, MainComponent.Child>(
        initialConfiguration = ChildConfig.AccountConnect,
        handleBackButton = true,
        childFactory = ::createChild
    )

    override var title by mutableStateOf(getTitle(router.state.value))

    override val state: RouterState<*, MainComponent.Child> by router.state.toComposeState(lifecycle)

    init {
        router.state.subscribe { state -> title = getTitle(state) }
    }

    private fun getTitle(routerState: RouterState<*, MainComponent.Child>): ResourceString {
        return LocalizedString.resource(
            when (routerState.activeChild.instance) {
                is MainComponent.Child.AccountConnect -> R.string.account_connect_title
                is MainComponent.Child.WalletConnect -> R.string.wallet_connect_title
                is MainComponent.Child.AccountPortfolio -> R.string.account_portfolio_title
                is MainComponent.Child.WalletPortfolio -> R.string.wallet_portfolio_title
            }
        )
    }

    private sealed interface ChildConfig : Parcelable {
        @Parcelize
        object AccountConnect : ChildConfig

        @Parcelize
        object WalletConnect : ChildConfig

        @Parcelize
        object AccountPortfolio : ChildConfig

        @Parcelize
        object WalletPortfolio : ChildConfig
    }

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ) = when (config) {
        is ChildConfig.AccountConnect -> MainComponent.Child.AccountConnect(
            componentFactory.createAccountConnectComponent(componentContext, ::onOpenWallet)
        )
        is ChildConfig.WalletConnect -> MainComponent.Child.WalletConnect(
            componentFactory.createWalletConnectComponent(componentContext)
        )
        is ChildConfig.AccountPortfolio -> MainComponent.Child.AccountPortfolio(
            componentFactory.createAccountPortfolioComponent(componentContext)
        )
        is ChildConfig.WalletPortfolio -> MainComponent.Child.WalletPortfolio(
            componentFactory.createWalletPortfolioComponent(componentContext)
        )
    }

    private fun onOpenWallet() {
        router.push(ChildConfig.WalletConnect)
    }

}