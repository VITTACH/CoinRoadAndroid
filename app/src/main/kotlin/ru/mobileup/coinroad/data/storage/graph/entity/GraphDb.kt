package ru.mobileup.coinroad.data.storage.graph.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.mobileup.coinroad.domain.common.*
import ru.mobileup.coinroad.ui.graph.ChartType

@Entity(tableName = "graphs")
data class GraphDb(
    @PrimaryKey val id: String,
    @Embedded(prefix = "exchange_") val exchange: ExchangeDb,
    @Embedded(prefix = "currencyPair_") val currencyPair: CurrencyPairDb,
    val timeStep: String,
    val isVisible: Boolean,
    @Embedded(prefix = "deepLink_") val deepLink: DeepLinkDb?,
    val updateTime: Long,
    val isMinMaxVisible: Boolean,
    val isTickerVisible: Boolean,
    val isAlertsVisible: Boolean,
    val chartType: Int
)

data class ExchangeDb(
    val id: String,
    val name: String
)

data class CurrencyPairDb(
    val id: String,
    @Embedded(prefix = "baseCurrency_") val baseCurrency: CurrencyDb,
    @Embedded(prefix = "quoteCurrency_") val quoteCurrency: CurrencyDb
)

data class CurrencyDb(
    val id: String,
    val name: String
)

data class DeepLinkDb(
    val id: String,
    val packageName: String
)

// To db

fun Graph.toDb() = GraphDb(
    id = id,
    exchange = exchange.toDb(),
    currencyPair = currencyPair.toDb(),
    timeStep = timeStep.name,
    isVisible = isVisible,
    deepLink = deepLink?.toDb(),
    updateTime = updateTime,
    isMinMaxVisible = isMinMaxVisible,
    isTickerVisible = isTickerVisible,
    isAlertsVisible = isAlertsVisible,
    chartType = chartType.ordinal
)

fun DeepLink.toDb() = DeepLinkDb(id, packageName)

fun Exchange.toDb() = ExchangeDb(id.name, name)

fun CurrencyPair.toDb() = CurrencyPairDb(
    id = id,
    baseCurrency = baseCurrency.toDb(),
    quoteCurrency = quoteCurrency.toDb()
)

fun Currency.toDb() = CurrencyDb(id, name)

// To domain

fun GraphDb.toDomain(graphData: GraphData?, alerts: List<Alert>) = Graph(
    id = id,
    exchange = exchange.toDomain(),
    currencyPair = currencyPair.toDomain(),
    timeStep = enumValueOf(timeStep),
    isVisible = isVisible,
    deepLink = deepLink?.toDomain(),
    chartType = ChartType.values()[chartType],
    graphData = graphData,
    updateTime = updateTime,
    isMinMaxVisible = isMinMaxVisible,
    isTickerVisible = isTickerVisible,
    isAlertsVisible = isAlertsVisible,
    alerts = alerts
)

fun DeepLinkDb.toDomain() = DeepLink(id, packageName)

fun ExchangeDb.toDomain() = Exchange.fromId(Exchange.Ids.valueOf(id.uppercase()))

fun CurrencyPairDb.toDomain() = CurrencyPair(
    id = id,
    baseCurrency = baseCurrency.toDomain(),
    quoteCurrency = quoteCurrency.toDomain()
)

fun CurrencyDb.toDomain() = Currency(id, name)