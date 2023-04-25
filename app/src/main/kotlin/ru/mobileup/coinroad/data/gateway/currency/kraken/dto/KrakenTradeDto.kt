package ru.mobileup.coinroad.data.gateway.currency.kraken.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.mobileup.coinroad.domain.common.Trade
import ru.mobileup.coinroad.util.serializer.KrakenTradeSerializer

@Serializable(with = KrakenTradeSerializer::class)
data class KrakenPublicTrades(val trades: List<Trade>)

@Serializable
data class KrakenTradesDto(
    @SerialName("result") val result: KrakenPublicTrades
)

fun KrakenTradesDto.toTrades(): List<Trade> = result.trades