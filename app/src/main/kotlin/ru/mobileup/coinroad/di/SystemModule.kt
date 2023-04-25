package ru.mobileup.coinroad.di

import androidx.datastore.preferences.createDataStore
import me.aartikov.sesame.navigation.NavigationMessageDispatcher
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module
import ru.mobileup.coinroad.BuildConfig
import ru.mobileup.coinroad.analytics.AnalyticsManager
import ru.mobileup.coinroad.analytics.performers.AmplitudeAnalyticPerformer
import ru.mobileup.coinroad.analytics.performers.FirebaseAnalyticPerformer
import ru.mobileup.coinroad.navigation.SystemBackNavigator
import ru.mobileup.coinroad.subscription.BillingManager
import ru.mobileup.coinroad.subscription.BillingProvider
import ru.mobileup.coinroad.subscription.BillingProviderImpl
import ru.mobileup.coinroad.subscription.GoogleBillingManager
import ru.mobileup.coinroad.system.DeviceInfo
import ru.mobileup.coinroad.system.NotificationWorkManager
import ru.mobileup.coinroad.system.ResourceHelper
import ru.mobileup.coinroad.util.helper.SwipeHelper
import ru.mobileup.coinroad.util.helper.WebTabHelper

val systemModule = module {

    single { androidContext().createDataStore(name = BuildConfig.APPLICATION_ID) }
    single { SystemBackNavigator() }
    single { NavigationMessageDispatcher() }
    worker { NotificationWorkManager(get(), get(), get(), get(), get(), get(), get(), get()) }

    // Analytics
    single { AmplitudeAnalyticPerformer() }
    single { FirebaseAnalyticPerformer(get()) }
    single { AnalyticsManager(get(), get(), get(), get(), get(), get()) }

    // Helper
    single { WebTabHelper() }
    single { SwipeHelper() }
    single { ResourceHelper(get()) }
    single { DeviceInfo(get()) }

    // Billing
    single<BillingManager> { GoogleBillingManager(get()) }
    single<BillingProvider> { BillingProviderImpl(get()) }
}