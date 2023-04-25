package ru.mobileup.coinroad.ui.graph_creating

import ru.mobileup.coinroad.domain.common.Exchange
import java.io.Serializable

data class GraphCreatingData(
    val exchange: Exchange,
    val isAlert: Boolean = false
): Serializable