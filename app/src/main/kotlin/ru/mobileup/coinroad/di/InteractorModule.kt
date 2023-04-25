package ru.mobileup.coinroad.di

import org.koin.dsl.module
import ru.mobileup.coinroad.domain.common.BarComposer
import ru.mobileup.coinroad.domain.usecase.advert.LoadAdvertsInteractor
import ru.mobileup.coinroad.domain.usecase.alert.EditAlertsInteractor
import ru.mobileup.coinroad.domain.usecase.alert.LoadAlertsInteractor
import ru.mobileup.coinroad.domain.usecase.billing.LoadAdsPricesInteractor
import ru.mobileup.coinroad.domain.usecase.billing.LoadAdsPurchaseInteractor
import ru.mobileup.coinroad.domain.usecase.currency.LoadCurrencyPairsInteractor
import ru.mobileup.coinroad.domain.usecase.exchange.LoadExchangesInteractor
import ru.mobileup.coinroad.domain.usecase.graph.EditGraphsInteractor
import ru.mobileup.coinroad.domain.usecase.graph.HasGraphsInteractor
import ru.mobileup.coinroad.domain.usecase.graph.LoadGraphsInteractor
import ru.mobileup.coinroad.domain.usecase.portfolio.LoadAccountsInteractor
import ru.mobileup.coinroad.domain.usecase.portfolio.LoadWalletsInteractor
import ru.mobileup.coinroad.domain.usecase.settings.*
import ru.mobileup.coinroad.domain.usecase.widget.EditWidgetsInteractor

val interactorModule = module {
    factory { LoadAccountsInteractor(get()) }
    factory { LoadExchangesInteractor(get()) }
    factory { LoadWalletsInteractor(get()) }
    factory { LoadAdvertsInteractor(get()) }
    factory { LoadCurrencyPairsInteractor(get(), get()) }

    // Charts
    factory { BarComposer() }
    factory { LoadGraphsInteractor(get(), get(), get(), get(), get()) }
    factory { HasGraphsInteractor(get()) }
    factory { EditGraphsInteractor(get(), get()) }

    // Settings
    factory { GetScreenshotInteractor(get()) }
    factory { SaveScreenshotInteractor(get()) }
    factory { GetSettingsInteractor(get()) }
    factory { UpdateThemeInteractor(get()) }
    factory { EditChartHeightInteractor(get()) }
    factory { UpdatePeriodInteractor(get()) }
    factory { WidgetHelpInteractor(get()) }
    factory { FeaturesInteractor(get()) }

    // Alerts
    factory { AlertSensitiveInteractor(get()) }
    factory { LoadAlertsInteractor(get()) }
    factory { EditAlertsInteractor(get()) }

    // Widgets
    factory { EditWidgetsInteractor(get()) }

    // Billing
    factory { LoadAdsPurchaseInteractor(get()) }
    factory { LoadAdsPricesInteractor(get()) }
}