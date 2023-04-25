package ru.mobileup.coinroad.ui.connection

import ru.mobileup.coinroad.domain.common.Connection

sealed class ConnectionDetailsData {
    open val connection: Connection? = null
    open val onClickListener: (Connection) -> Unit = {}
}

object EmptyConnectionItemData : ConnectionDetailsData()

data class NewConnectionItemData(
    override val connection: Connection,
    override val onClickListener: (Connection) -> Unit
) : ConnectionDetailsData()

data class ConnectionItemData(
    override val connection: Connection,
    override val onClickListener: (Connection) -> Unit
) : ConnectionDetailsData()