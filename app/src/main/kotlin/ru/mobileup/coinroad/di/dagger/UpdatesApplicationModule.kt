package ru.mobileup.coinroad.di.dagger

import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.tasks.TaskExecutors
import dagger.Module
import dagger.Provides
import ru.mobileup.coinroad.App
import java.util.concurrent.Executor

@Module
object UpdatesApplicationModule {
    @Provides
    fun providesInAppUpdateManager(application: App): AppUpdateManager {
        return AppUpdateManagerFactory.create(application)
    }

    @Provides
    fun providesPlayServiceExecutor(): Executor {
        return TaskExecutors.MAIN_THREAD
    }
}
