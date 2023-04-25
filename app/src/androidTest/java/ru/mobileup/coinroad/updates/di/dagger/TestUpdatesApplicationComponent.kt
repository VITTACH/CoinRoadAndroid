package ru.mobileup.coinroad.updates.di.dagger

import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import ru.mobileup.coinroad.App
import ru.mobileup.coinroad.di.dagger.ScreenModule
import ru.mobileup.coinroad.di.dagger.UpdatesApplicationComponent
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ScreenModule::class,
        FakeUpdatesApplicationModule::class
    ]
)
interface TestUpdatesApplicationComponent : UpdatesApplicationComponent {

    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<App>

    fun fakeAppUpdateManager(): FakeAppUpdateManager
}
