package ru.mobileup.coinroad.domain.usecase.settings

import kotlinx.coroutines.flow.first
import ru.mobileup.coinroad.data.storage.settings.SettingsStorage

class AlertSensitiveInteractor(
    private val settingsStorage: SettingsStorage
) {

    suspend fun execute(alertSensitive: Int) {
        settingsStorage.saveSettings(
            settingsStorage.getSettings().first().copy(alertSensitive = alertSensitive)
        )
    }
}