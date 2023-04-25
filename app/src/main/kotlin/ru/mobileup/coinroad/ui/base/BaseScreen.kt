package ru.mobileup.coinroad.ui.base

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.work.WorkManager
import me.aartikov.sesame.dialog.DialogObserver
import me.aartikov.sesame.property.PropertyObserver
import ru.mobileup.coinroad.system.NotificationWorkManager

abstract class BaseScreen<VM : BaseViewModel>(
    @LayoutRes contentLayoutId: Int,
) : Fragment(contentLayoutId), PropertyObserver, DialogObserver {

    override val propertyObserverLifecycleOwner: LifecycleOwner get() = viewLifecycleOwner
    override val dialogObserverLifecycleOwner: LifecycleOwner get() = viewLifecycleOwner

    protected abstract val vm: VM
    protected open val snackBarAnchor: View get() = requireView()
    protected open val useResumeForActivable: Boolean = false

    private val notificationManager by lazy {
        requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    protected val workManager by lazy { WorkManager.getInstance(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.bindToScreen()
    }

    protected fun <T : BaseViewModel> T.bindToScreen() {
        BindingDelegate(
            fragment = this@BaseScreen,
            snackBarAnchor = snackBarAnchor,
            useResumeForActivable = useResumeForActivable
        ).bind(this)
    }

    open fun onBackPressed() {
        vm.navigateBack()
    }

    protected fun cancelNotificationChannel() = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(NotificationWorkManager.GRAPHS_CHANNEL_ID)
        }
        notificationManager.cancelAll()
    } catch (e: Exception) {
    }
}