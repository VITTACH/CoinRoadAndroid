package ru.mobileup.coinroad.ui.compose.portfolio.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.mobileup.coinroad.ui.compose.core.ui.theme.AppTheme

@Composable
fun AccountPortfolioUi(
    accountPortfolioComponent: AccountPortfolioComponent,
    modifier: Modifier = Modifier
) {
}

@Preview(showSystemUi = true)
@Composable
fun AccountPortfolioUiPreview() {
    AppTheme { AccountPortfolioUi(FakeAccountPortfolioPortfolioComponent()) }
}

class FakeAccountPortfolioPortfolioComponent : AccountPortfolioComponent