package ru.mobileup.coinroad.util.serializer

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import ru.mobileup.coinroad.data.gateway.currency.kraken.dto.KrakenPublicTrades
import ru.mobileup.coinroad.domain.common.Trade
import java.math.BigDecimal

object KrakenTradeSerializer : KSerializer<KrakenPublicTrades> {
    override val descriptor = PrimitiveSerialDescriptor("KrakenPublicTrades", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): KrakenPublicTrades {
        val trades = mutableListOf<Trade>()

        val jsonInput = decoder as? JsonDecoder ?: error("Can't deserialize")
        val json = jsonInput.decodeJsonElement().jsonObject
        val tradesResultIterator = json.entries.iterator()

        while (tradesResultIterator.hasNext()) {
            val entry: Map.Entry<String, JsonElement> = tradesResultIterator.next()
            val key = entry.key
            val value = entry.value
            if (key != "last" && value is JsonArray) {
                for (tradeJsonNode in value.jsonArray) {
                    val node = tradeJsonNode as JsonArray
                    val price = BigDecimal(node[0].jsonPrimitive.content)
                    val originalTime = node[2].jsonPrimitive.double

                    val time = Instant.fromEpochSeconds(
                        originalTime.toLong(),
                        ((originalTime * 1000) % 1000).toLong()
                    )

                    trades.add(Trade(time = time, price = price))
                }
            }
        }

        return KrakenPublicTrades(trades)
    }

    override fun serialize(encoder: Encoder, value: KrakenPublicTrades) {
    }
}