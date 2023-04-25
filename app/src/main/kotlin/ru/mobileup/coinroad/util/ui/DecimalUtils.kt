package ru.mobileup.coinroad.util

import com.androidplot.xy.CandlestickSeries
import ru.mobileup.coinroad.ui.graph.GraphDataItem
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

private const val MIN_PRICE_PRECISION = 0

fun CandlestickSeries.getTickerPrice(): Double {
    return closeSeries.getyVals().lastOrNull() as? Double ?: 0.0
}

fun GraphDataItem.formatPrice(price: BigDecimal, grouping: Boolean = true) = formatNumber(
    price,
    ticker.precision,
    MIN_PRICE_PRECISION,
    grouping
)

private fun integerDigits(n: BigDecimal?): Int {
    if (n == null) return 0
    return if (n.signum() == 0) 1 else n.precision() - n.scale()
}

private val decimalFormatter = object : ThreadLocal<DecimalFormat>() {
    override fun initialValue(): DecimalFormat =
        DecimalFormat().apply {
            roundingMode = RoundingMode.HALF_DOWN
            decimalFormatSymbols = DecimalFormatSymbols().apply {
                decimalSeparator = '.'
            }
        }
}

fun formatNumber(
    value: BigDecimal,
    maxFractionDigits: Int,
    minFractionDigits: Int = MIN_PRICE_PRECISION,
    grouping: Boolean = true
): String {
    return decimalFormatter.get()!!.apply {
        groupingSize = if (grouping) 3 else 0
        minimumFractionDigits = minFractionDigits
        maximumFractionDigits = maxFractionDigits
    }.format(value)
}

fun formatNumber(
    value: BigDecimal?,
    maxFractionDigits: Int?,
    minFractionDigits: Int?,
    grouping: Boolean = true
): String? {
    if (value == null || minFractionDigits == null || maxFractionDigits == null) return null
    return formatNumber(value, maxFractionDigits, minFractionDigits, grouping)
}


fun formatNumber(
    value: BigDecimal?,
    fractionDigits: Int?
): String? {
    if (value == null || fractionDigits == null) return null
    return formatNumber(value, fractionDigits, fractionDigits)
}

fun formatNumber(
    value: BigDecimal,
    fractionDigits: Int
): String = formatNumber(value, fractionDigits, fractionDigits)

fun formatValuePercentChange(
    minuend: BigDecimal,
    subtrahend: BigDecimal,
    fractionDigits: Int
) = try {
    val valueDiff = minuend - subtrahend
    val percent = (valueDiff / subtrahend * BigDecimal("100"))

    val sign = when (valueDiff.signum()) {
        -1 -> "-"
        1 -> "+"
        else -> ""
    }

    StringBuilder().append(sign)
        .append(formatNumber(valueDiff.abs(), fractionDigits))
        .append(" (")
        .append(formatNumber(percent.abs(), 2))
        .append("%)")
        .toString()
} catch (e: Exception) {
    null
}

fun formatValuePercentChange(
    minuend: BigDecimal?,
    subtrahend: BigDecimal?,
    fractionDigits: Int?
): String? {
    if (minuend == null || subtrahend == null || fractionDigits == null) return null
    return formatValuePercentChange(minuend, subtrahend, fractionDigits)
}

fun calculatePercentDifference(val1: BigDecimal, val2: BigDecimal): BigDecimal = try {
    ((val1 - val2) / val2 * BigDecimal(100))
} catch (e: ArithmeticException) {
    BigDecimal.ZERO
}

fun getPricePattern(precision: Int): Regex {
    return ("[0-9]+((\\.[0-9]{0,$precision})?)||(\\.)?").toRegex()
}