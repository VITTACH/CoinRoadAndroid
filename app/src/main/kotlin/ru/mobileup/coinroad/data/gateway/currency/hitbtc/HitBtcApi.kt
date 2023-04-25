package ru.mobileup.coinroad.data.gateway.currency.hitbtc

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.mobileup.coinroad.data.gateway.currency.hitbtc.dto.HitBtcBarDto
import ru.mobileup.coinroad.data.gateway.currency.hitbtc.dto.HitBtcCurrencyPairDto
import ru.mobileup.coinroad.data.gateway.currency.hitbtc.dto.HitBtcTradeDto
import ru.mobileup.coinroad.data.gateway.ticker.hitbtc.dto.HitBtcTickerDto


/**
 * https://api.hitbtc.com/
 */
interface HitBtcApi {

    @GET("symbol")
    suspend fun getCurrencyPairs(): List<HitBtcCurrencyPairDto>

    @GET("trades/{symbol}")
    suspend fun getTrades(
        @Path("symbol") symbol: String,
        @Query("limit") limit: Long,
    ): List<HitBtcTradeDto>

    @GET("ticker/{symbol}")
    suspend fun getTicker(
        @Path("symbol") symbol: String
    ): HitBtcTickerDto

    @GET("candles/{symbol}")
    suspend fun getBars(
        @Path("symbol") symbol: String,
        @Query("period") period: String,
        @Query("sort") sort: String = "DESC"
    ): List<HitBtcBarDto>
}