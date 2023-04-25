package ru.mobileup.coinroad.analytics

data class AnalyticEvent(
    val category: String,
    val action: String,
    val label: Any,
    val deviceId: String = ""
)