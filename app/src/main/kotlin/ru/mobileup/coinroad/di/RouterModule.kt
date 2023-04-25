package ru.mobileup.coinroad.di

import org.koin.dsl.module
import ru.mobileup.coinroad.analytics.exchange.ExchangeAnalyticRouter
import ru.mobileup.coinroad.analytics.push.PushAnalyticRouter
import ru.mobileup.coinroad.analytics.system.SystemAnalyticRouter
import ru.mobileup.coinroad.navigation.FragmentNavigator
import ru.mobileup.coinroad.navigation.alerts.AlertRouter
import ru.mobileup.coinroad.navigation.graphs.GraphRouter
import ru.mobileup.coinroad.navigation.profile.ProfileRouter
import ru.mobileup.coinroad.navigation.settings.SettingsRouter
import ru.mobileup.coinroad.navigation.system.SystemRouter

val routerModule = module {
    // Navigation
    factory { (navigator: FragmentNavigator) -> ProfileRouter(navigator) }
    factory { (navigator: FragmentNavigator) -> SettingsRouter(navigator) }
    factory { (navigator: FragmentNavigator) -> SystemRouter(navigator, get(), get()) }
    factory { (navigator: FragmentNavigator) -> AlertRouter(navigator) }
    factory { (navigator: FragmentNavigator) -> GraphRouter(navigator) }

    // Analytics
    factory { PushAnalyticRouter() }
    factory { SystemAnalyticRouter() }
    factory { ExchangeAnalyticRouter() }
}