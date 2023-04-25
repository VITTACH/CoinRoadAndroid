package ru.mobileup.coinroad.di.dagger

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import ru.mobileup.coinroad.App
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ScreenModule::class,
        UpdatesApplicationModule::class
    ]
)
interface UpdatesApplicationComponent : AndroidInjector<App> {
    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<App>
}
