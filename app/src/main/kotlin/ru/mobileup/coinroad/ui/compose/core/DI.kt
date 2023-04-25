package ru.mobileup.coinroad.ui.compose.core

import org.koin.dsl.module
import ru.mobileup.coinroad.ui.compose.core.ui.ComponentFactory

val coreModule = module {

    scope<ComponentFactory> {
    }
}