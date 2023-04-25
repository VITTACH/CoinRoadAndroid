package ru.mobileup.coinroad.data.storage.alert

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.mobileup.coinroad.data.storage.alert.entity.toDb
import ru.mobileup.coinroad.data.storage.alert.entity.toDomain
import ru.mobileup.coinroad.domain.common.Alert

class RoomAlertStorage(private val alertDao: AlertDao) : AlertStorage {

    private val deletedAlertIds = MutableStateFlow<Set<String>>(emptySet())

    private val mutex = Mutex()

    override fun observeAlerts(): Flow<List<Alert>> {
        return alertDao.observeAlerts().map { db ->
            db.map { it.toDomain() }
        }
    }

    override suspend fun deleteAlert(alertId: String) {
        alertDao.deleteAlert(alertId)
        mutex.withLock {
            deletedAlertIds.value = deletedAlertIds.value.toMutableSet().apply { add(alertId) }
        }
    }

    override suspend fun saveAlert(alert: Alert) {
        mutex.withLock {
            if (!deletedAlertIds.value.contains(alert.id)) {
                alertDao.insertAlert(alert.toDb())
            }
        }
    }
}