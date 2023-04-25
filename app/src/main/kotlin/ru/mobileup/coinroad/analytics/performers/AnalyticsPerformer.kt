package ru.mobileup.coinroad.analytics.performers

import ru.mobileup.coinroad.analytics.AnalyticEvent

interface AnalyticsPerformer {
    fun perform(event: AnalyticEvent)
}
