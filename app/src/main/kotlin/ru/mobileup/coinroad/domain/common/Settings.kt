package ru.mobileup.coinroad.domain.common

enum class Periods(val value: Int) {
    MIN_1(60),
    MIN_5(300),
    MIN_15(900),
    MIN_30(1800);

    companion object {
        fun getPeriod(value: Int): Int {
            return values()[value].value
        }
    }
}

data class Settings(
    val chartHeight: Int,
    val updatePeriod: Int,
    val alertSensitive: Int,
    val isNightMode: Boolean,
    val isWidgetHelpShown: Boolean,
    val isFeaturesShown: Boolean
) {
    companion object {
        const val MAX_CHART_HEIGHT = 132
        const val MIN_CHART_HEIGHT = 64

        val DEFAULT = Settings(
            chartHeight = MAX_CHART_HEIGHT,
            updatePeriod = 60,
            alertSensitive = 1,
            isNightMode = true,
            isWidgetHelpShown = false,
            isFeaturesShown = false
        )
    }
}