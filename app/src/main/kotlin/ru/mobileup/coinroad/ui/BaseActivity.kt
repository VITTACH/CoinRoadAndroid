package ru.mobileup.coinroad.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.aartikov.sesame.navigation.NavigationMessage
import me.aartikov.sesame.navigation.NavigationMessageHandler
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.navigation.FragmentNavigator
import ru.mobileup.coinroad.navigation.SystemBackNavigator
import ru.mobileup.coinroad.navigation.alerts.AlertRouter
import ru.mobileup.coinroad.navigation.graphs.GraphRouter
import ru.mobileup.coinroad.navigation.profile.ProfileRouter
import ru.mobileup.coinroad.navigation.settings.SettingsRouter
import ru.mobileup.coinroad.navigation.system.SystemRouter
import ru.mobileup.coinroad.ui.base.BaseScreen
import ru.mobileup.coinroad.util.helper.WebTabHelper
import timber.log.Timber

open class BaseActivity : AppCompatActivity(), NavigationMessageHandler {

    private val navigator by lazy { FragmentNavigator(R.id.container, supportFragmentManager) }

    private val webTabHelper: WebTabHelper by inject()
    private val systemBackNavigator: SystemBackNavigator by inject()

    private val systemRouter by inject<SystemRouter> { parametersOf(navigator) }
    private val profileRouter by inject<ProfileRouter> { parametersOf(navigator) }
    private val settingsRouter by inject<SettingsRouter> { parametersOf(navigator) }
    private val alertRouter by inject<AlertRouter> { parametersOf(navigator) }
    private val graphRouter by inject<GraphRouter> { parametersOf(navigator) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webTabHelper.attach(this)
        systemBackNavigator.attach(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        systemBackNavigator.detach(this)
        webTabHelper.detach(this)
    }

    override fun onBackPressed() {
        (navigator.currentScreen as? BaseScreen<*>)?.onBackPressed()
    }

    override fun handleNavigationMessage(message: NavigationMessage): Boolean {
        Timber.d("New navigationMessage = $message")

        return when {
            systemRouter.handleNavigationMessage(message) -> true
            profileRouter.handleNavigationMessage(message) -> true
            settingsRouter.handleNavigationMessage(message) -> true
            alertRouter.handleNavigationMessage(message) -> true
            graphRouter.handleNavigationMessage(message) -> true
            else -> {
                Timber.d("Unhandled navigation message %s", message)
                false
            }
        }
    }
}