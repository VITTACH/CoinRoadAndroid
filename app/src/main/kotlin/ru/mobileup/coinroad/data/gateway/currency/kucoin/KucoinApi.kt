package ru.mobileup.coinroad.data.gateway.currency.kucoin

import retrofit2.http.GET
import retrofit2.http.Query
import ru.mobileup.coinroad.data.gateway.currency.kucoin.dto.KucoinAssetPairsDto
import ru.mobileup.coinroad.data.gateway.currency.kucoin.dto.KucoinBarDto
import ru.mobileup.coinroad.data.gateway.currency.kucoin.dto.KucoinTradeDto
import ru.mobileup.coinroad.data.gateway.ticker.kucoin.dto.KucoinTickerDto


/**
 * https://api.kucoin.com/api/v1
 */
interface KucoinApi {

    @GET("symbols")
    suspend fun getCurrencyPairs(): KucoinAssetPairsDto

    @GET("market/histories")
    suspend fun getTrades(
        @Query("symbol") symbol: String,
    ): KucoinTradeDto

    @GET("market/stats")
    suspend fun getTicker(
        @Query("symbol") symbol: String
    ): KucoinTickerDto

    @GET("market/candles")
    suspend fun getBars(
        @Query("symbol") symbol: String,
        @Query("type") period: String
    ): KucoinBarDto
}