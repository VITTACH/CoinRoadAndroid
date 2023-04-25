package ru.mobileup.coinroad.domain.common

import kotlinx.datetime.Instant

sealed class Connection {

    data class Wallet(val id: String) : Connection()

    data class Account(
        val exchange: Exchange,
        val id: String = "",
        val lastSyncTime: Instant? = null,
        val isEnabled: Boolean = true
    ) : Connection()
}