package ru.mobileup.coinroad.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.aartikov.sesame.activable.Activable
import me.aartikov.sesame.navigation.NavigationMessage
import me.aartikov.sesame.navigation.NavigationMessageQueue
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.command
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.domain.exception.NetworkException
import ru.mobileup.coinroad.domain.exception.ServerErrorException
import ru.mobileup.coinroad.navigation.system.Back
import ru.mobileup.coinroad.system.ResourceHelper

open class BaseViewModel : ViewModel(), PropertyHost, KoinComponent, Activable by Activable() {

    override val propertyHostScope get() = viewModelScope

    protected val resourceHelper: ResourceHelper by inject()

    val navigationMessageQueue = NavigationMessageQueue()
    val showSnackbar = command<String>()
    val showHidingSnackbar = command<String>()
    val showActionSnackbar = command<SnackbarAction>()

    open fun navigateBack() = navigate(Back)

    protected fun navigate(message: NavigationMessage) {
        navigationMessageQueue.send(message)
    }

    protected fun safeLaunch(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    protected fun handleException(error: Throwable, action: () -> Unit = {}) = when (error) {
        is NetworkException -> {
            val errorMessage = resourceHelper.getString(R.string.error_no_internet_connection)
            val errorTitle = resourceHelper.getString(R.string.error_retry)
            showActionSnackbar(SnackbarAction(errorMessage, errorTitle) { action.invoke() })
        }
        is ServerErrorException -> {
            showSnackbar(resourceHelper.getString(R.string.error_unexpected))
        }
        else -> {
            showSnackbar(error.message ?: resourceHelper.getString(R.string.error_unexpected))
        }
    }
}