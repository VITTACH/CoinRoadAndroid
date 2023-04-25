package ru.mobileup.coinroad.data.gateway.currency.binance

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.mobileup.coinroad.data.gateway.currency.binance.dto.BinanceAssetPairsDto
import ru.mobileup.coinroad.data.gateway.currency.binance.dto.BinanceBarDto
import ru.mobileup.coinroad.data.gateway.currency.binance.dto.BinanceTradeDto
import ru.mobileup.coinroad.data.gateway.ticker.binance.dto.BinanceTickerDto


/**
 * https://api.binance.com/api/v3
 */
interface BinanceApi {

    @GET("exchangeInfo")
    suspend fun getCurrencyPairs(): BinanceAssetPairsDto

    @GET("trades")
    suspend fun getTrades(
        @Query("symbol") symbol: String,
        @Query("limit") limit: Long,
    ): List<BinanceTradeDto>

    @GET("ticker/24hr")
    suspend fun getTicker(
        @Query("symbol") symbol: String
    ): BinanceTickerDto

    @GET("klines")
    suspend fun getBars(
        @Query("symbol") symbol: String,
        @Query("interval") period: String,
        @Query("limit") limit: Long
    ): List<BinanceBarDto>
}