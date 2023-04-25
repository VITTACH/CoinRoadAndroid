package ru.mobileup.coinroad.analytics.exchange

import ru.mobileup.coinroad.analytics.AnalyticMessage
import ru.mobileup.coinroad.domain.common.Currency
import ru.mobileup.coinroad.domain.common.Exchange

data class ReportExchangeSelect(val exchange: Exchange) : AnalyticMessage
data class ReportFirstCurrencySelect(val currency: Currency) : AnalyticMessage
data class ReportSecondCurrencySelect(val currency: Currency) : AnalyticMessage