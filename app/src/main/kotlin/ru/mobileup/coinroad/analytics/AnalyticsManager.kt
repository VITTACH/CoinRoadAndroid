package ru.mobileup.coinroad.analytics

import ru.mobileup.coinroad.analytics.exchange.ExchangeAnalyticRouter
import ru.mobileup.coinroad.analytics.performers.AmplitudeAnalyticPerformer
import ru.mobileup.coinroad.analytics.performers.FirebaseAnalyticPerformer
import ru.mobileup.coinroad.analytics.push.PushAnalyticRouter
import ru.mobileup.coinroad.analytics.system.SystemAnalyticRouter
import ru.mobileup.coinroad.system.DeviceInfo
import timber.log.Timber


class AnalyticsManager(
    amplitudeAnalyticPerformer: AmplitudeAnalyticPerformer,
    firebaseAnalyticPerformer: FirebaseAnalyticPerformer,
    private val exchangeAnalyticRouter: ExchangeAnalyticRouter,
    private val systemAnalyticRouter: SystemAnalyticRouter,
    private val pushAnalyticRouter: PushAnalyticRouter,
    private val deviceInfo: DeviceInfo
) {
    companion object {
        const val NEW_USER_CATEGORY = "new_user"
        const val EXCHANGE_CATEGORY = "exchange"
        const val WORK_MANAGER_CATEGORY = "work_manager"
        const val PUSH_CATEGORY = "push"
        const val SCREEN_CATEGORY = "screen"
        const val SETTINGS_CATEGORY = "settings"
    }

    private val analytics = arrayOf(firebaseAnalyticPerformer, amplitudeAnalyticPerformer)

    fun handleAnalyticMessage(message: AnalyticMessage): Boolean {
        Timber.d("New analytic message = $message")

        return when {
            reportEvent(systemAnalyticRouter.handleAnalyticMessage(message)) -> true
            reportEvent(pushAnalyticRouter.handleAnalyticMessage(message)) -> true
            reportEvent(exchangeAnalyticRouter.handleAnalyticMessage(message)) -> true
            else -> {
                Timber.d("Unhandled navigation message %s", message)
                false
            }
        }
    }

    private fun reportEvent(event: AnalyticEvent?): Boolean {
        return event?.let {
            analytics.forEach { it.perform(event.copy(deviceId = deviceInfo.deviceId)) }
            true
        } ?: false
    }
}