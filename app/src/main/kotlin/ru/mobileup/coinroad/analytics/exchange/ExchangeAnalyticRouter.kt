package ru.mobileup.coinroad.analytics.exchange;

import ru.mobileup.coinroad.analytics.AnalyticEvent
import ru.mobileup.coinroad.analytics.AnalyticMessage
import ru.mobileup.coinroad.analytics.AnalyticMessageHandler
import ru.mobileup.coinroad.analytics.AnalyticsManager.Companion.EXCHANGE_CATEGORY

class ExchangeAnalyticRouter : AnalyticMessageHandler {

    override fun handleAnalyticMessage(message: AnalyticMessage): AnalyticEvent? {
        return when (message) {
            is ReportExchangeSelect -> {
                AnalyticEvent(EXCHANGE_CATEGORY, "Выбор_биржи", message.exchange.toString())
            }

            is ReportFirstCurrencySelect -> {
                AnalyticEvent(EXCHANGE_CATEGORY, "Выбор_первой_валюты", message.currency.toString())
            }

            is ReportSecondCurrencySelect -> {
                AnalyticEvent(EXCHANGE_CATEGORY, "Выбор_второй_валюты", message.currency.toString())
            }

            else -> null
        }
    }
}