package ru.mobileup.coinroad.ui.dashboard.content

import ru.mobileup.coinroad.domain.common.Graph

sealed class GraphsContent {
    object Empty : GraphsContent()
    object Cancel : GraphsContent()
    object Loading : GraphsContent()
    data class Data(val graphs: List<Graph>, val refreshing: Boolean) : GraphsContent()
}