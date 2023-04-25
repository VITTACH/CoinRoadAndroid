package ru.mobileup.coinroad.data.storage.amplitude

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreAmplitudeStorage(
    private val store: DataStore<Preferences>
) : AmplitudeStorage {

    companion object {
        private val IS_FIRST_LAUNCH_KEY = booleanPreferencesKey("is_first_launch_key")
    }

    override suspend fun isFirstLaunch(): Boolean {
        val isFirstLaunch = store.data.map { it[IS_FIRST_LAUNCH_KEY] ?: true }.first()

        if (isFirstLaunch) store.edit { prefs -> prefs[IS_FIRST_LAUNCH_KEY] = false }

        return isFirstLaunch
    }

    override suspend fun resetFirstLaunch() {
        store.edit { it[IS_FIRST_LAUNCH_KEY] = true }
    }
}