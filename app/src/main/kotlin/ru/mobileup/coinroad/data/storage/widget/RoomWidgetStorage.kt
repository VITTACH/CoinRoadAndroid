package ru.mobileup.coinroad.data.storage.widget

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.mobileup.coinroad.data.storage.widget.entity.toDb
import ru.mobileup.coinroad.data.storage.widget.entity.toDomain
import ru.mobileup.coinroad.domain.common.Widget

class RoomWidgetStorage(
    private val widgetDao: WidgetDao,
    private val store: DataStore<Preferences>
) : WidgetStorage {

    companion object {
        private val PERIOD_KEY = intPreferencesKey("period_key")
    }

    override fun readPeriod(): Flow<Int> {
        return store.data.map { it[PERIOD_KEY] ?: 0 }
    }

    override suspend fun savePeriod(period: Int) {
        store.edit { it[PERIOD_KEY] = period }
    }

    override fun observeWidgets(): Flow<List<Widget>> {
        return widgetDao.observeWidgets().map { dbWidgets -> dbWidgets.map { it.toDomain() } }
    }

    override suspend fun deleteWidget(widgetId: Int) {
        widgetDao.deleteWidget(widgetId)
    }

    override suspend fun saveWidget(widget: Widget) {
        widgetDao.insertWidget(widget.toDb())
    }
}
