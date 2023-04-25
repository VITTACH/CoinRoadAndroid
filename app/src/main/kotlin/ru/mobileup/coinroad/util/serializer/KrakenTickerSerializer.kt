package ru.mobileup.coinroad.util.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import ru.mobileup.coinroad.data.gateway.ticker.kraken.dto.KrakenTicker
import java.math.BigDecimal

object KrakenTickerSerializer : KSerializer<KrakenTicker> {
    override val descriptor = PrimitiveSerialDescriptor("KrakenTicker", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): KrakenTicker {
        var lowest24hPrice = BigDecimal.ZERO
        var biggest24hPrice = BigDecimal.ZERO

        val jsonInput = decoder as? JsonDecoder ?: error("Can't deserialize")
        val json = jsonInput.decodeJsonElement().jsonObject
        var tickerResultIterator = json.entries.iterator()

        var entry: Map.Entry<String, JsonElement> = tickerResultIterator.next()

        tickerResultIterator = entry.value.jsonObject.entries.iterator()

        while (tickerResultIterator.hasNext()) {
            entry = tickerResultIterator.next()
            val key = entry.key
            val value = entry.value
            when (key) {
                "l" -> lowest24hPrice =
                    BigDecimal((value as JsonArray)[1].toString().replace("\"", ""))
                "h" -> biggest24hPrice =
                    BigDecimal((value as JsonArray)[1].toString().replace("\"", ""))
            }
        }

        return KrakenTicker(
            lowest24hPrice = lowest24hPrice,
            biggest24hPrice = biggest24hPrice
        )
    }

    override fun serialize(encoder: Encoder, value: KrakenTicker) {
    }
}