package ru.mobileup.coinroad.domain.usecase.alert

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.mobileup.coinroad.data.storage.alert.AlertStorage
import ru.mobileup.coinroad.domain.common.Alert

class LoadAlertsInteractor(
    private val alertStorage: AlertStorage
) {
    suspend fun load(fresh: Boolean = false): List<Alert> = coroutineScope {
        alertStorage.observeAlerts().first()
    }

    fun observe(): Flow<List<Alert>> {
        return alertStorage.observeAlerts()
    }
}