package ru.mobileup.coinroad.di.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.mobileup.coinroad.ui.MainActivity

@Module
interface ScreenModule {
    @ContributesAndroidInjector
    fun contributesMainActivity(): MainActivity
}
