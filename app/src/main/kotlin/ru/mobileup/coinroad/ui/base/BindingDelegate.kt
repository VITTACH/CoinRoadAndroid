package ru.mobileup.coinroad.ui.base

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import me.aartikov.sesame.activable.bindToLifecycle
import me.aartikov.sesame.dialog.DialogObserver
import me.aartikov.sesame.navigation.NavigationMessageDispatcher
import me.aartikov.sesame.navigation.bind
import me.aartikov.sesame.property.PropertyObserver
import org.koin.core.component.KoinComponent
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.system.ResourceHelper
import ru.mobileup.coinroad.util.system.bindToResumeLifecycle

class BindingDelegate(
    override val propertyObserverLifecycleOwner: LifecycleOwner,
    override val dialogObserverLifecycleOwner: LifecycleOwner,
    private val dispatcherNode: Any,
    private val snackBarAnchor: View,
    private val useResumeForActivable: Boolean
) : PropertyObserver, DialogObserver, KoinComponent {

    private val SNACKBAR_LENGTH = 4000

    constructor(activity: AppCompatActivity, snackBarAnchor: View) : this(
        activity,
        activity,
        activity,
        snackBarAnchor,
        useResumeForActivable = false
    )

    constructor(fragment: Fragment, snackBarAnchor: View, useResumeForActivable: Boolean) : this(
        fragment.viewLifecycleOwner,
        fragment.viewLifecycleOwner,
        fragment,
        snackBarAnchor,
        useResumeForActivable
    )

    private val navigationMessageDispatcher: NavigationMessageDispatcher = getKoin().get()
    private val resourceHelper: ResourceHelper = getKoin().get()

    fun bind(viewModel: BaseViewModel) {
        viewModel.showSnackbar bind { showSimpleSnackbar(snackBarAnchor, it) }
        viewModel.showActionSnackbar bind {
            showActionSnackbar(
                snackBarAnchor,
                it.message,
                it.actionTitle,
                it.action
            )
        }
        viewModel.showHidingSnackbar bind { showActionSnackbar(snackBarAnchor, it) }
        viewModel.navigationMessageQueue.bind(
            navigationMessageDispatcher,
            dispatcherNode,
            propertyObserverLifecycleOwner
        )

        if (useResumeForActivable) {
            viewModel.bindToResumeLifecycle(propertyObserverLifecycleOwner.lifecycle)
        } else {
            viewModel.bindToLifecycle(propertyObserverLifecycleOwner.lifecycle)
        }
    }

    private fun showSimpleSnackbar(snackBarAnchor: View, text: String) {
        Snackbar.make(snackBarAnchor, text, Snackbar.LENGTH_LONG)
            .apply {
                view.background = resourceHelper.getDrawable(R.drawable.background_snackbar)
            }
            .show()
    }

    private fun showActionSnackbar(
        snackBarAnchor: View,
        text: String,
        actionText: String = resourceHelper.getString(R.string.hide),
        action: (() -> Unit)? = null
    ) {
        Snackbar.make(snackBarAnchor, text, SNACKBAR_LENGTH)
            .apply {
                view.background = resourceHelper.getDrawable(R.drawable.background_snackbar)
                setAction(actionText) {
                    if (action != null) {
                        action.invoke()
                    } else {
                        this.dismiss()
                    }
                }
            }
            .show()
    }
}