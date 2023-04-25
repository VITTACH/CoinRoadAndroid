package ru.mobileup.coinroad.domain.usecase.settings

import kotlinx.coroutines.flow.first
import ru.mobileup.coinroad.data.storage.settings.SettingsStorage

class FeaturesInteractor(
    private val settingsStorage: SettingsStorage
) {

    suspend fun execute() {
        settingsStorage.saveSettings(
            settingsStorage.getSettings().first().copy(isFeaturesShown = true)
        )
    }
}