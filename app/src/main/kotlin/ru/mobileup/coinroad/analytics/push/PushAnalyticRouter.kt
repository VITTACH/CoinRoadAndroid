package ru.mobileup.coinroad.analytics.push;

import ru.mobileup.coinroad.analytics.AnalyticEvent
import ru.mobileup.coinroad.analytics.AnalyticMessage
import ru.mobileup.coinroad.analytics.AnalyticMessageHandler
import ru.mobileup.coinroad.analytics.AnalyticsManager.Companion.PUSH_CATEGORY
import ru.mobileup.coinroad.domain.analytics.toAnalytic

class PushAnalyticRouter : AnalyticMessageHandler {

    override fun handleAnalyticMessage(message: AnalyticMessage): AnalyticEvent? {
        return when (message) {
            is ReportPushCreate -> {
                AnalyticEvent(PUSH_CATEGORY, "Создание_пуша", message.pushModel.toAnalytic())
            }

            is ReportPushRemove -> {
                AnalyticEvent(PUSH_CATEGORY, "Удаление_пуша", message.pushModel.toAnalytic())
            }

            is ReportPushVisibility -> if (message.isVisible) {
                AnalyticEvent(PUSH_CATEGORY, "Включение_пуша", message.pushModel.toAnalytic())
            } else {
                AnalyticEvent(PUSH_CATEGORY, "Отключение_пуша", message.pushModel.toAnalytic())
            }

            is ReportMinMaxVisibility -> if (message.isVisible) {
                AnalyticEvent(PUSH_CATEGORY, "Включение_MinMax", message.pushModel.toAnalytic())
            } else {
                AnalyticEvent(PUSH_CATEGORY, "Отключение_MinMax", message.pushModel.toAnalytic())
            }

            else -> return null
        }
    }
}