package ru.mobileup.coinroad.data.storage.settings

import kotlinx.coroutines.flow.Flow
import ru.mobileup.coinroad.domain.common.Settings

interface SettingsStorage {

    fun getSettings(): Flow<Settings>

    suspend fun saveSettings(settings: Settings)
}