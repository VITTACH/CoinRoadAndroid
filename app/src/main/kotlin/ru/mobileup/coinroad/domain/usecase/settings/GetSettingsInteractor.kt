package ru.mobileup.coinroad.domain.usecase.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.mobileup.coinroad.data.storage.settings.SettingsStorage
import ru.mobileup.coinroad.domain.common.Settings

class GetSettingsInteractor(
    private val settingsStorage: SettingsStorage
) {

    suspend fun load(fresh: Boolean = false): Settings {
        return settingsStorage.getSettings().first()
    }

    fun observe(): Flow<Settings> {
        return settingsStorage.getSettings()
    }
}