package ru.mobileup.coinroad.domain.usecase.settings

import kotlinx.coroutines.flow.first
import ru.mobileup.coinroad.data.storage.settings.SettingsStorage

class UpdatePeriodInteractor(
    private val settingsStorage: SettingsStorage
) {

    suspend fun execute(updatePeriod: Int) {
        settingsStorage.saveSettings(
            settingsStorage.getSettings().first().copy(updatePeriod = updatePeriod)
        )
    }
}