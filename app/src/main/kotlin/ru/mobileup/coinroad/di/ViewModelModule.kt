package ru.mobileup.coinroad.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.mobileup.coinroad.ui.compose.ComposeMainViewModel
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.ui.MainActivityViewModel
import ru.mobileup.coinroad.ui.about.AboutViewModel
import ru.mobileup.coinroad.ui.alerts.AlertsViewModel
import ru.mobileup.coinroad.ui.alerts.NewAlertData
import ru.mobileup.coinroad.ui.alerts.NewAlertViewModel
import ru.mobileup.coinroad.ui.connection.SelectConnectionViewModel
import ru.mobileup.coinroad.ui.dashboard.DashboardViewModel
import ru.mobileup.coinroad.ui.exchange.ExchangeChoosingViewModel
import ru.mobileup.coinroad.ui.exchange.SelectExchangeViewModel
import ru.mobileup.coinroad.ui.graph.ChartInitViewModel
import ru.mobileup.coinroad.ui.graph_creating.GraphCreatingData
import ru.mobileup.coinroad.ui.graph_creating.GraphCreatingViewModel
import ru.mobileup.coinroad.ui.graph_editing.GraphEditingViewModel
import ru.mobileup.coinroad.ui.main.MainViewModel
import ru.mobileup.coinroad.ui.portfolio.PortfolioViewModel
import ru.mobileup.coinroad.ui.settings.SettingsViewModel
import ru.mobileup.coinroad.ui.trading.TradingViewModel
import ru.mobileup.coinroad.widget.WidgetActivityViewModel

val viewModelModule = module {
    viewModel { DashboardViewModel(androidContext(), get(), get(), get(), get(), get()) }
    viewModel { WidgetActivityViewModel(get(), get(), get(), get()) }
    viewModel { ChartInitViewModel(get()) }
    viewModel { ExchangeChoosingViewModel(get()) }
    viewModel { AboutViewModel(get()) }
    viewModel { AlertsViewModel(get(), get()) }
    viewModel { (data: NewAlertData) -> NewAlertViewModel(data, get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }

    viewModel { MainViewModel() }
    viewModel { MainActivityViewModel(get(), get(), get(), get()) }
    viewModel { TradingViewModel() }
    viewModel { PortfolioViewModel(get(), get()) }
    viewModel { ComposeMainViewModel() }

    viewModel { (graph: Graph) ->
        GraphEditingViewModel(graph, get(), get(), get(), get(), get(), get(), get(), get())
    }
    viewModel { (data: GraphCreatingData) ->
        GraphCreatingViewModel(data, get(), get(), get(), get(), get(), get(), get(), get())
    }
    viewModel { SelectExchangeViewModel(get()) }
    viewModel { SelectConnectionViewModel(get(), get(), get()) }
}