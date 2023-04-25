package ru.mobileup.coinroad.domain.common

import kotlinx.datetime.Instant
import java.math.BigDecimal

/**
 * Fact that some [CurrencyPair] was bought/sold
 * @param time time of a trade
 * @param price price of a currency pair
 */
data class Trade(
    val time: Instant,
    val price: BigDecimal
)