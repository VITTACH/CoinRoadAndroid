package ru.mobileup.coinroad.ui.graph_creating

import ru.mobileup.coinroad.domain.common.Currency

sealed class CurrencyContent {
    object Empty: CurrencyContent()
    data class Data(val list: List<Currency>, val refreshing: Boolean): CurrencyContent()
}