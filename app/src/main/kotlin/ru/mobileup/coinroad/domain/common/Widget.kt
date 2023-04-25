package ru.mobileup.coinroad.domain.common

data class Widget(
    val id: Int,
    val exchange: Exchange,
    val currencyPair: CurrencyPair
)