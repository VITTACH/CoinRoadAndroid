package ru.mobileup.coinroad.ui.graph_creating

import ru.mobileup.coinroad.domain.common.Graph

sealed class PreviewContent {
    object Empty: PreviewContent()
    data class PreviewGraph(val graph: Graph): PreviewContent()
}