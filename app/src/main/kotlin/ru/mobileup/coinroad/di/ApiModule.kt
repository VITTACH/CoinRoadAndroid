package ru.mobileup.coinroad.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import ru.mobileup.coinroad.BuildConfig
import ru.mobileup.coinroad.DebugToolsInitializer
import ru.mobileup.coinroad.data.gateway.currency.binance.BinanceApi
import ru.mobileup.coinroad.data.gateway.currency.hitbtc.HitBtcApi
import ru.mobileup.coinroad.data.gateway.currency.kickex.KickExApi
import ru.mobileup.coinroad.data.gateway.currency.kraken.KrakenApi
import ru.mobileup.coinroad.data.gateway.currency.kucoin.KucoinApi
import timber.log.Timber
import java.util.concurrent.TimeUnit

val apiModule = module {
    single<BinanceApi> { createApi("https://api.binance.com/api/v3/", get(), get()) }
    single<HitBtcApi> { createApi("https://api.hitbtc.com/api/2/public/", get(), get()) }
    single<KrakenApi> { createApi("https://api.kraken.com/0/public/", get(), get()) }
    single<KickExApi> { createApi("https://gate.kickex.com/api/v1/", get(), get()) }
    single<KucoinApi> { createApi("https://api.kucoin.com/api/v1/", get(), get()) }
    single { createJson() }
    single { createHttpClient() }
}

private fun createJson(): Json {
    return Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        isLenient = true
    }
}

private fun createHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().apply {
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(60, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            addNetworkInterceptor(loggingInterceptor())
            DebugToolsInitializer.interceptor?.let { addInterceptor(it) }
        }
    }.build()
}

private inline fun <reified T> createApi(
    baseUrl: String,
    client: OkHttpClient,
    json: Json
): T {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(T::class.java)
}

private fun loggingInterceptor(): Interceptor {
    return HttpLoggingInterceptor { message -> Timber.tag("OkHttp").d(message) }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}