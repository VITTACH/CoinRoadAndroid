package ru.mobileup.coinroad.ui.alerts.content

import ru.mobileup.coinroad.domain.common.Alert

sealed class AlertsContent {
    object Loading : AlertsContent()
    data class Empty(val hasFilter: Boolean = false) : AlertsContent()
    data class Data(val alerts: List<Alert>, val refreshing: Boolean) : AlertsContent()
}