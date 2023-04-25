package ru.mobileup.coinroad.domain.usecase.settings

import kotlinx.coroutines.flow.first
import ru.mobileup.coinroad.data.storage.settings.SettingsStorage

class EditChartHeightInteractor(
    private val settingsStorage: SettingsStorage
) {

    suspend fun execute(chartHeight: Int) {
        settingsStorage.saveSettings(
            settingsStorage.getSettings().first().copy(chartHeight = chartHeight)
        )
    }
}