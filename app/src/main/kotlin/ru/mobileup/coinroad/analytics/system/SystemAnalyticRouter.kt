package ru.mobileup.coinroad.analytics.system;

import ru.mobileup.coinroad.analytics.AnalyticEvent
import ru.mobileup.coinroad.analytics.AnalyticMessage
import ru.mobileup.coinroad.analytics.AnalyticMessageHandler
import ru.mobileup.coinroad.analytics.AnalyticsManager.Companion.NEW_USER_CATEGORY
import ru.mobileup.coinroad.analytics.AnalyticsManager.Companion.SCREEN_CATEGORY
import ru.mobileup.coinroad.analytics.AnalyticsManager.Companion.WORK_MANAGER_CATEGORY
import ru.mobileup.coinroad.domain.common.toAnalytic

class SystemAnalyticRouter : AnalyticMessageHandler {

    override fun handleAnalyticMessage(message: AnalyticMessage): AnalyticEvent? {
        return when (message) {
            is ReportFirstLaunch -> {
                AnalyticEvent(NEW_USER_CATEGORY, "Новый пользователь", "Первый запуск приложения")
            }

            is ReportMobileUpSiteClick -> {
                AnalyticEvent(SCREEN_CATEGORY, "Нажатие_на_ссылку", "website_mobileUp")
            }

            is ReportAboutScreenShown -> {
                AnalyticEvent(SCREEN_CATEGORY, "Переход_на_экран", "show_about_screen")
            }

            is ReportWorkManagerStatus -> {
                AnalyticEvent(
                    WORK_MANAGER_CATEGORY,
                    "Фоновое_обновление_графика",
                    message.graph.toAnalytic()
                )
            }

            else -> return null
        }
    }
}