package ru.mobileup.coinroad.di

import org.koin.dsl.module
import ru.mobileup.coinroad.data.storage.CoinRoadDatabase
import ru.mobileup.coinroad.data.storage.account.AccountStorage
import ru.mobileup.coinroad.data.storage.account.RoomAccountStorage
import ru.mobileup.coinroad.data.storage.advert.AdvertStorage
import ru.mobileup.coinroad.data.storage.advert.AdvertStorageImpl
import ru.mobileup.coinroad.data.storage.alert.AlertStorage
import ru.mobileup.coinroad.data.storage.alert.RoomAlertStorage
import ru.mobileup.coinroad.data.storage.amplitude.AmplitudeStorage
import ru.mobileup.coinroad.data.storage.amplitude.DataStoreAmplitudeStorage
import ru.mobileup.coinroad.data.storage.currency.CurrencyPairStorage
import ru.mobileup.coinroad.data.storage.currency.InMemoryCurrencyPairStorage
import ru.mobileup.coinroad.data.storage.graph.GraphStorage
import ru.mobileup.coinroad.data.storage.graph.RoomGraphStorage
import ru.mobileup.coinroad.data.storage.graph.chart.ChartBitmapStorage
import ru.mobileup.coinroad.data.storage.graph.chart.InMemoryChartBitmapStorage
import ru.mobileup.coinroad.data.storage.screenshot.InMemoryScreenshotStorage
import ru.mobileup.coinroad.data.storage.screenshot.ScreenshotStorage
import ru.mobileup.coinroad.data.storage.settings.DataStoreSettingsStorage
import ru.mobileup.coinroad.data.storage.settings.SettingsStorage
import ru.mobileup.coinroad.data.storage.wallet.RoomWalletStorage
import ru.mobileup.coinroad.data.storage.wallet.WalletStorage
import ru.mobileup.coinroad.data.storage.widget.RoomWidgetStorage
import ru.mobileup.coinroad.data.storage.widget.WidgetStorage

val storageModule = module {
    single { CoinRoadDatabase.create(get()) }

    single<ScreenshotStorage> { InMemoryScreenshotStorage() }
    single<AmplitudeStorage> { DataStoreAmplitudeStorage(get()) }
    single<CurrencyPairStorage> { InMemoryCurrencyPairStorage() }
    single<ChartBitmapStorage> { InMemoryChartBitmapStorage() }

    single<WalletStorage> { RoomWalletStorage(get()) }
    single<AccountStorage> { RoomAccountStorage(get()) }
    single<AdvertStorage> { AdvertStorageImpl(get()) }
    single<AlertStorage> { RoomAlertStorage(get()) }
    single<GraphStorage> { RoomGraphStorage(get(), get()) }
    single<SettingsStorage> { DataStoreSettingsStorage(get()) }
    single<WidgetStorage> { RoomWidgetStorage(get(), get()) }

    single { get<CoinRoadDatabase>().getGraphDao() }
    single { get<CoinRoadDatabase>().getWidgetDao() }
    single { get<CoinRoadDatabase>().getAlertDao() }
    single { get<CoinRoadDatabase>().getAccountDao() }
    single { get<CoinRoadDatabase>().getWalletDao() }
}