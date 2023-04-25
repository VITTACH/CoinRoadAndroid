package ru.mobileup.coinroad.domain.common

import java.math.BigDecimal

/**
 * Contains price information for [CurrencyPair]
 * @param precision precision of CurrencyPair
 * @param exchange market for the trading
 * @param currencyPair currency pair of current ticker
 * @param priceChangeForLastDay delta of prices for last day
 * @param priceChangeInPercentForLastDay delta of prices in percents for last day
 */
data class Ticker(
    val exchange: Exchange,
    val currencyPair: CurrencyPair,
    val priceChangeForLastDay: BigDecimal,
    val priceChangeInPercentForLastDay: Float,
    val precision: Int = DEFAULT_PRECISION
) {
    companion object {
        private const val DEFAULT_PRECISION = 8

        val DEFAULT = Ticker(
            exchange = Exchange.DEFAULT,
            currencyPair = CurrencyPair.fromSymbol("-/-"),
            priceChangeForLastDay = BigDecimal.ONE,
            priceChangeInPercentForLastDay = 0f
        )
    }
}