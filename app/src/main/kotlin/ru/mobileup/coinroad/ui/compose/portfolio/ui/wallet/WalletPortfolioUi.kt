package ru.mobileup.coinroad.ui.compose.portfolio.ui.wallet

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.mobileup.coinroad.ui.compose.core.ui.theme.AppTheme

@Composable
fun WalletPortfolioUi(
    walletPortfolioComponent: WalletPortfolioComponent,
    modifier: Modifier = Modifier
) {
}

@Preview(showSystemUi = true)
@Composable
fun WalletPortfolioUiPreview() {
    AppTheme { WalletPortfolioUi(FakeWalletPortfolioComponent()) }
}

class FakeWalletPortfolioComponent : WalletPortfolioComponent