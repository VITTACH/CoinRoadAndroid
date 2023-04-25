package ru.mobileup.coinroad.ui.compose.core.ui

import android.app.Activity
import org.koin.core.Koin
import org.koin.core.component.KoinScopeComponent

/**
 * Используется для создания Decompose-компонентов.
 * Сами методы для компонентов реализуются как extension-функции в feature-модулях.
 *
 * ComponentFactory создает koin-вский scope, привязанный к времени жизни активити.
 * В этом скоупе есть доступ до экземпляра активити.
 */
class ComponentFactory(koin: Koin, activity: Activity) : KoinScopeComponent {

    override val scope = koin.createScope(this)

    init {
        scope.declare(this)
        scope.declare(activity)
    }
}