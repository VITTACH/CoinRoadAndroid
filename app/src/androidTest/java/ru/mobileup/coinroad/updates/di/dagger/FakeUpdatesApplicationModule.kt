package ru.mobileup.coinroad.updates.di.dagger

import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import dagger.Module
import dagger.Provides
import ru.mobileup.coinroad.App
import java.util.concurrent.Executor
import javax.inject.Singleton

@Module
object FakeUpdatesApplicationModule {

    @Singleton
    @Provides
    fun providesFakeInAppUpdateManager(application: App): FakeAppUpdateManager {
        return FakeAppUpdateManager(application)
    }

    @Provides
    fun providesInAppUpdateManager(fakeAppUpdateManager: FakeAppUpdateManager): AppUpdateManager {
        return fakeAppUpdateManager
    }

    @Provides
    fun providesPlayServiceExecutor(): Executor {
        return Executor { it.run() }
    }
}
