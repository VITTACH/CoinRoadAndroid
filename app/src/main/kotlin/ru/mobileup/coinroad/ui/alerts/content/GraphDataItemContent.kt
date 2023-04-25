package ru.mobileup.coinroad.ui.alerts.content

import ru.mobileup.coinroad.ui.graph.GraphDataItem

sealed class GraphDataItemContent {
    object Empty : GraphDataItemContent()
    data class Data(val dataItem: GraphDataItem) : GraphDataItemContent()
}