package ru.mobileup.coinroad.data.storage.alert

import kotlinx.coroutines.flow.Flow
import ru.mobileup.coinroad.domain.common.Alert

interface AlertStorage {

    fun observeAlerts(): Flow<List<Alert>>

    suspend fun saveAlert(alert: Alert)

    suspend fun deleteAlert(alertId: String)
}