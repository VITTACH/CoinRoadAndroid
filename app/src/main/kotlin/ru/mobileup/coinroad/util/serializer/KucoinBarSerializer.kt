package ru.mobileup.coinroad.util.serializer

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import ru.mobileup.coinroad.data.gateway.currency.kucoin.dto.KucoinBar
import ru.mobileup.coinroad.domain.common.Bar
import java.math.BigDecimal

object KucoinBarSerializer : KSerializer<KucoinBar> {
    override val descriptor = PrimitiveSerialDescriptor("BinancePublicBars", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): KucoinBar {
        val jsonInput = decoder as? JsonDecoder ?: error("Can't deserialize")
        val node = (jsonInput.decodeJsonElement() as JsonArray)

        val time = Instant.fromEpochSeconds(node[0].jsonPrimitive.long)
        val openPrice = BigDecimal(node[1].jsonPrimitive.content)
        val highPrice = BigDecimal(node[3].jsonPrimitive.content)
        val lowPrice = BigDecimal(node[4].jsonPrimitive.content)
        val closePrice = BigDecimal(node[2].jsonPrimitive.content)

        val bar = Bar(time, lowPrice, highPrice, openPrice, closePrice)
        return KucoinBar(bar)
    }

    override fun serialize(encoder: Encoder, value: KucoinBar) {
    }
}