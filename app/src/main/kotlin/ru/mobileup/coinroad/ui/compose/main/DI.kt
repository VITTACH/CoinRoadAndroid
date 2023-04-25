package ru.mobileup.coinroad.ui.compose.main

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import org.koin.dsl.module
import ru.mobileup.coinroad.ui.compose.core.ui.ComponentFactory
import ru.mobileup.coinroad.ui.compose.main.ui.MainComponent
import ru.mobileup.coinroad.ui.compose.main.ui.RealMainComponent

val rootModule = module {
}

fun ComponentFactory.createMainComponent(
    componentContext: ComponentContext
): MainComponent {
    return RealMainComponent(componentContext, get())
}