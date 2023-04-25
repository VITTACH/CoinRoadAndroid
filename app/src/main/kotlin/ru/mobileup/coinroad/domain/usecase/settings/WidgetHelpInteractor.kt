package ru.mobileup.coinroad.domain.usecase.settings

import kotlinx.coroutines.flow.first
import ru.mobileup.coinroad.data.storage.settings.SettingsStorage

class WidgetHelpInteractor(
    private val settingsStorage: SettingsStorage
) {

    suspend fun execute() {
        settingsStorage.saveSettings(
            settingsStorage.getSettings().first().copy(isWidgetHelpShown = true)
        )
    }
}