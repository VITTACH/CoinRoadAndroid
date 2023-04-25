package ru.mobileup.coinroad.ui.compose.connections.ui.wallet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.mobileup.coinroad.ui.compose.core.ui.theme.AppTheme

@Composable
fun WalletConnectUi(
    walletConnectComponent: WalletConnectComponent,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
    }
}

@Preview(showSystemUi = true)
@Composable
fun WalletConnectUiPreview() {
    AppTheme { WalletConnectUi(FakeWalletConnectComponent()) }
}

class FakeWalletConnectComponent : WalletConnectComponent