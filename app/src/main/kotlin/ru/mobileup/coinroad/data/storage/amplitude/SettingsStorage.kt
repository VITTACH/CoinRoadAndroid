package ru.mobileup.coinroad.data.storage.amplitude


interface AmplitudeStorage {

    suspend fun isFirstLaunch(): Boolean

    suspend fun resetFirstLaunch()
}