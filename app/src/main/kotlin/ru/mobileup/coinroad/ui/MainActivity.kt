package ru.mobileup.coinroad.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.LifecycleOwner
import androidx.work.WorkManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.ajalt.timberkt.w
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus.DOWNLOADED
import com.google.android.play.core.install.model.InstallStatus.FAILED
import com.google.android.play.core.install.model.UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
import com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
import dagger.android.AndroidInjection
import me.aartikov.sesame.navigation.NavigationMessageDispatcher
import me.aartikov.sesame.property.PropertyObserver
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent.getKoin
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ActivityMainBinding
import ru.mobileup.coinroad.ui.base.BindingDelegate
import ru.mobileup.coinroad.ui.graph.ChartInitActivity
import ru.mobileup.coinroad.util.system.dataOrNull
import ru.mobileup.coinroad.util.helper.updateSystemUI
import ru.mobileup.coinroad.util.startWork
import java.util.concurrent.Executor
import javax.inject.Inject


class MainActivity : BaseActivity(), PropertyObserver {

    companion object {
        private const val IMMEDIATE_UPDATE_REQUEST_CODE = 1867
        private const val FLEXIBLE_UPDATE_REQUEST_CODE = 4244
    }

    override val propertyObserverLifecycleOwner: LifecycleOwner get() = this

    private val workManager by lazy { WorkManager.getInstance(application) }

    private val binding by viewBinding(ActivityMainBinding::bind, R.id.root)

    private val navigationMessageDispatcher: NavigationMessageDispatcher = getKoin().get()
    private val vm: MainActivityViewModel by viewModel()

    @Inject
    lateinit var appUpdateManager: AppUpdateManager

    @Inject
    lateinit var playServiceExecutor: Executor

    private val updateListener = { state: InstallState ->
        if (state.installStatus() == DOWNLOADED) {
            vm.showUpdateComplete { appUpdateManager.completeUpdate() }
        } else if (state.installStatus() == FAILED) {
            vm.showUpdateRetry { checkInAppUpdate() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_main)

        BindingDelegate(this, binding.snackbarLayout).bind(vm)

        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                workManager.startWork()
            }
        }.launch(Intent(this, ChartInitActivity::class.java))

        if (savedInstanceState == null) {
            vm.appLaunched()
        }

        vm::settingsState bind { state ->
            state.dataOrNull?.run { setTheme(isNightMode) }
        }

        appUpdateManager.registerListener(updateListener)
        checkInAppUpdate()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigationMessageDispatcher.resume()
    }

    override fun onPause() {
        navigationMessageDispatcher.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        resumeUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.unregisterListener(updateListener)
    }

    private fun resumeUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener(playServiceExecutor, {
            if (it.installStatus() == DOWNLOADED) {
                vm.showUpdateComplete()
            }
        })
    }

    private fun checkInAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener(playServiceExecutor, {
            when (it.updateAvailability()) {
                DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> startImmediateUpdate(it)

                UPDATE_AVAILABLE -> when {
                    it.isUpdateTypeAllowed(FLEXIBLE) -> startFlexibleUpdate(it)
                    it.isUpdateTypeAllowed(IMMEDIATE) -> startImmediateUpdate(it)
                }

                else -> {
                    // Nothing
                }
            }
        }).addOnFailureListener {
            w { "checkInAppUpdate failed: $it" }
        }
    }

    private fun startImmediateUpdate(info: AppUpdateInfo) {
        appUpdateManager
            .startUpdateFlowForResult(info, IMMEDIATE, this, IMMEDIATE_UPDATE_REQUEST_CODE)
    }

    private fun startFlexibleUpdate(info: AppUpdateInfo) {
        appUpdateManager
            .startUpdateFlowForResult(info, FLEXIBLE, this, FLEXIBLE_UPDATE_REQUEST_CODE)
    }

    private fun setTheme(isNightMode: Boolean) {
        val themeMode = if (isNightMode) MODE_NIGHT_YES else MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(themeMode)
        updateSystemUI(isNightMode)
    }
}