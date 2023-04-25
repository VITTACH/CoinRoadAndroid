package ru.mobileup.coinroad.ui.compose.main.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.jetpack.Children
import com.arkivanov.decompose.router.RouterState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.aartikov.sesame.localizedstring.LocalizedString
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.ui.compose.connections.ui.account.AccountConnectUi
import ru.mobileup.coinroad.ui.compose.connections.ui.account.FakeAccountConnectComponent
import ru.mobileup.coinroad.ui.compose.connections.ui.wallet.WalletConnectUi
import ru.mobileup.coinroad.ui.compose.core.ui.theme.AppTheme
import ru.mobileup.coinroad.ui.compose.core.ui.utils.createFakeRouterState
import ru.mobileup.coinroad.ui.compose.core.ui.utils.resolve

@Composable
fun MainUi(component: MainComponent, modifier: Modifier = Modifier) {
    val backgroundColor = MaterialTheme.colors.background

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopBar(component.title, backgroundColor) }
    ) {
        Surface(
            color = backgroundColor,
            modifier = modifier.fillMaxSize()
        ) {
            Content(component.state)
        }
    }
}

@Composable
private fun TopBar(title: LocalizedString, backgroundColor: Color) {
    TopAppBar(
        backgroundColor = backgroundColor,
        title = { Text(title.resolve()) }
    )
}

@Composable
private fun Content(routerState: RouterState<*, MainComponent.Child>) {
    Children(routerState) { child ->
        when (val instance = child.instance) {
            is MainComponent.Child.AccountConnect -> AccountConnectUi(instance.component)
            is MainComponent.Child.WalletConnect -> WalletConnectUi(instance.component)
        }
    }
}

@Composable
private fun setupSystemUi(backgroundColor: Color) {
    val systemUiController = rememberSystemUiController()
    val surfaceColor = MaterialTheme.colors.surface
    SideEffect {
        systemUiController.setStatusBarColor(color = backgroundColor)
        systemUiController.setNavigationBarColor(color = surfaceColor)
    }
}

@Preview(showSystemUi = true)
@Composable
fun MainUiPreview() {
    AppTheme { MainUi(FakeMainComponent()) }
}

class FakeMainComponent : MainComponent {
    override val title = LocalizedString.resource(R.string.account_connect_title)

    override val state = createFakeRouterState(
        MainComponent.Child.AccountConnect(FakeAccountConnectComponent())
    )
}