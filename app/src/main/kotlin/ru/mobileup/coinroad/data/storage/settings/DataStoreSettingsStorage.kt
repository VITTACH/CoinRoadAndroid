package ru.mobileup.coinroad.data.storage.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.map
import ru.mobileup.coinroad.domain.common.Settings
import ru.mobileup.coinroad.domain.common.Settings.Companion.DEFAULT

class DataStoreSettingsStorage(
    private val store: DataStore<Preferences>
) : SettingsStorage {

    private companion object {
        val ALERT_SENSITIVE_KEY = intPreferencesKey("alert_sensitive_key")
        val UPDATE_PERIOD_KEY = intPreferencesKey("update_period_key")
        val CHART_HEIGHT_KEY = intPreferencesKey("chart_height_key")
        val NIGHTMODE_ENABLED_KEY = booleanPreferencesKey("nightmode_enabled_key")
        val WIDGET_INFO_KEY = booleanPreferencesKey("widget_info_key")
        val PROMO_INFO_KEY = booleanPreferencesKey("promo_info_key")
    }

    override fun getSettings() = store.data.map {
        Settings(
            it[CHART_HEIGHT_KEY] ?: DEFAULT.chartHeight,
            it[UPDATE_PERIOD_KEY] ?: DEFAULT.updatePeriod,
            it[ALERT_SENSITIVE_KEY] ?: DEFAULT.alertSensitive,
            it[NIGHTMODE_ENABLED_KEY] ?: DEFAULT.isNightMode,
            it[WIDGET_INFO_KEY] ?: DEFAULT.isWidgetHelpShown,
            it[PROMO_INFO_KEY] ?: DEFAULT.isFeaturesShown
        )
    }

    override suspend fun saveSettings(settings: Settings) {
        store.edit {
            it[CHART_HEIGHT_KEY] = settings.chartHeight
            it[UPDATE_PERIOD_KEY] = settings.updatePeriod
            it[ALERT_SENSITIVE_KEY] = settings.alertSensitive
            it[NIGHTMODE_ENABLED_KEY] = settings.isNightMode
            it[WIDGET_INFO_KEY] = settings.isWidgetHelpShown
            it[PROMO_INFO_KEY] = settings.isFeaturesShown
        }
    }
}