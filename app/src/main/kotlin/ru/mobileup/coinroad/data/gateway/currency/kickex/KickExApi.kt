package ru.mobileup.coinroad.data.gateway.currency.kickex

import retrofit2.http.GET
import retrofit2.http.Query
import ru.mobileup.coinroad.data.gateway.currency.kickex.dto.KickExCurrencyPairDto
import ru.mobileup.coinroad.data.gateway.currency.kickex.dto.KickExTradeDto
import ru.mobileup.coinroad.data.gateway.currency.kickex.dto.KickExBarDto
import ru.mobileup.coinroad.data.gateway.ticker.kickex.dto.KickExTickerDto

/**
 * https://kickecosystem.github.io/KickEx-API-beta/
 */
interface KickExApi {

    /* Not working on 04.03.21 */
    @GET("market/stats24")
    suspend fun getTicker(@Query("pairName") symbol: String): KickExTickerDto

    @GET("market/pairs")
    suspend fun getCurrencyPairs(@Query("type") type: String = "market"): List<KickExCurrencyPairDto>

    @GET("market/trades")
    suspend fun getTrades(
        @Query("pairName") symbol: String,
        @Query("type") type: String = "all",
    ): List<KickExTradeDto>

    @GET("market/allTickers")
    suspend fun getTickers(): List<KickExTickerDto>

    @GET("market/bars")
    suspend fun getBars(
        @Query("pairName") symbol: String,
        @Query("period") period: Int,
    ): List<KickExBarDto>
}