package ru.mobileup.coinroad.ui.compose.connections.ui.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.coinroad.ui.compose.core.ui.theme.AppTheme

@Composable
fun AccountConnectUi(
    accountConnectComponent: AccountConnectComponent,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Button(
            onClick = { accountConnectComponent.onWalletClick() },
            modifier = Modifier.padding(16.dp)
        ) {
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun AccountConnectUiPreview() {
    AppTheme { AccountConnectUi(FakeAccountConnectComponent()) }
}

class FakeAccountConnectComponent : AccountConnectComponent {
    override fun onWalletClick() {}
}