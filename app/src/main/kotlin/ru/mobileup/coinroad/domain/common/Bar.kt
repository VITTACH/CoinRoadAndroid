package ru.mobileup.coinroad.domain.common

import kotlinx.datetime.Instant
import java.math.BigDecimal

/**
 * Represent bar of Bar Chart (similar to Japan candle)
 *
 * Ex.:
 *
 *                 - high       high -
 *             |                         |
 *             |__ - close      open - __|
 *             |                         |
 *             |                         |
 *             |                         |
 *    open - __|                         |__ - close
 *             |                         |
 *    low  -   |                         |   - low
 *
 * @property startTime start time of a bar.
 * @property lowPrice lowest price in a time interval.
 * @property highPrice highest price in a time interval.
 * @property openPrice price for beginning of a time interval.
 * @property closePrice price for ending of a time interval.
 */
data class Bar(
    val startTime: Instant,
    val lowPrice: BigDecimal,
    val highPrice: BigDecimal,
    val openPrice: BigDecimal,
    val closePrice: BigDecimal
)