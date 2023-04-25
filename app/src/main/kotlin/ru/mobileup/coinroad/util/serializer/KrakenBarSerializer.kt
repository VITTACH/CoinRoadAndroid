package ru.mobileup.coinroad.util.serializer

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import ru.mobileup.coinroad.data.gateway.currency.kraken.dto.KrakenPublicBars
import ru.mobileup.coinroad.domain.common.Bar
import java.math.BigDecimal

object KrakenBarSerializer : KSerializer<KrakenPublicBars> {
    override val descriptor = PrimitiveSerialDescriptor("KrakenPublicBars", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): KrakenPublicBars {
        val bars = mutableListOf<Bar>()

        val jsonInput = decoder as? JsonDecoder ?: error("Can't deserialize")
        val json = jsonInput.decodeJsonElement().jsonObject
        val iterator = json.entries.iterator()

        while (iterator.hasNext()) {
            val entry: Map.Entry<String, JsonElement> = iterator.next()
            val key = entry.key
            val value = entry.value
            if (key != "last" && value is JsonArray) {
                for (tradeJsonNode in value.jsonArray) {
                    val node = tradeJsonNode as JsonArray

                    val time = Instant.fromEpochSeconds(node[0].jsonPrimitive.long)
                    val openPrice = BigDecimal(node[1].jsonPrimitive.content)
                    val highPrice = BigDecimal(node[2].jsonPrimitive.content)
                    val lowPrice = BigDecimal(node[3].jsonPrimitive.content)
                    val closePrice = BigDecimal(node[4].jsonPrimitive.content)

                    bars.add(Bar(time, lowPrice, highPrice, openPrice, closePrice))
                }
            }
        }

        return KrakenPublicBars(bars)
    }

    override fun serialize(encoder: Encoder, value: KrakenPublicBars) {
    }
}