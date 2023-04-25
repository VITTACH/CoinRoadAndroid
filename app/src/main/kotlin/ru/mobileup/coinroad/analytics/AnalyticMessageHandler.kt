package ru.mobileup.coinroad.analytics

interface AnalyticMessage {
}

interface AnalyticMessageHandler {
    fun handleAnalyticMessage(message: AnalyticMessage): AnalyticEvent?
}
