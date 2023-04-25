package ru.mobileup.coinroad.domain.usecase.settings

import kotlinx.coroutines.flow.first
import ru.mobileup.coinroad.data.storage.settings.SettingsStorage

class UpdateThemeInteractor(
    private val settingsStorage: SettingsStorage
) {

    suspend fun execute(isDarkMode: Boolean) {
        settingsStorage.saveSettings(
            settingsStorage.getSettings().first().copy(isNightMode = isDarkMode)
        )
    }
}