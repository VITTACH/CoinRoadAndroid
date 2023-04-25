package ru.mobileup.coinroad.data.gateway.currency.kraken

import retrofit2.http.GET
import retrofit2.http.Query
import ru.mobileup.coinroad.data.gateway.currency.kraken.dto.KrakenAssetPairsDto
import ru.mobileup.coinroad.data.gateway.currency.kraken.dto.KrakenBarDto
import ru.mobileup.coinroad.data.gateway.currency.kraken.dto.KrakenTradesDto
import ru.mobileup.coinroad.data.gateway.ticker.kraken.dto.KrakenTickerDto


/**
 * https://www.kraken.com/features/api
 */
interface KrakenApi {

    @GET("AssetPairs")
    suspend fun getCurrencyPairs(): KrakenAssetPairsDto

    @GET("Trades")
    suspend fun getTrades(
        @Query("pair") symbol: String
    ): KrakenTradesDto

    @GET("Ticker")
    suspend fun getTicker(
        @Query("pair") symbol: String
    ): KrakenTickerDto

    @GET("OHLC")
    suspend fun getBars(
        @Query("pair") symbol: String,
        @Query("interval") interval: Int
    ): KrakenBarDto
}