package ru.mobileup.coinroad

import com.amplitude.api.Amplitude
import com.google.android.gms.ads.MobileAds
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import ru.mobileup.coinroad.ui.compose.composeModules
import ru.mobileup.coinroad.di.*
import ru.mobileup.coinroad.di.dagger.DaggerUpdatesApplicationComponent
import timber.log.Timber


class App : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        initKoin()
        initLogger()
        initAmplitude()
        initDebugTools()
        initGoogleAds()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerUpdatesApplicationComponent
            .factory()
            .create(this)
    }

    private fun initKoin() = startKoin {
        androidContext(this@App)
        workManagerFactory()
        modules(getAllModules())
    }

    private fun initDebugTools() {
        if (BuildConfig.DEBUG) {
            DebugToolsInitializer.initialize(this)
        }
    }

    private fun getAllModules() = listOf(
        storageModule,
        gatewayModule,
        routerModule,
        interactorModule,
        systemModule,
        apiModule,
        viewModelModule
    ) + composeModules

    private fun initAmplitude() {
        Amplitude
            .getInstance(BuildConfig.AMPLITUDE_INSTANCE_NAME)
            .initialize(this, BuildConfig.AMPLITUDE_API_KEY)
            .trackSessionEvents(true)
            .enableForegroundTracking(this)
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initGoogleAds() {
        MobileAds.initialize(this) { }
    }
}