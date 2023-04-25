package ru.mobileup.coinroad.updates

import android.os.SystemClock
import androidx.test.core.app.ActivityScenario.launch
import androidx.appcompat.widget.AppCompatButton
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.core.AllOf.allOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import ru.mobileup.coinroad.ui.MainActivity
import ru.mobileup.coinroad.updates.di.dagger.TestInjector
import ru.mobileup.coinroad.updates.di.dagger.TestUpdatesApplicationComponent

@RunWith(AndroidJUnit4::class)
class MainActivityTest: KoinTest {

    private lateinit var fakeAppUpdateManager: FakeAppUpdateManager

    @Before
    fun setup() {
        val component: TestUpdatesApplicationComponent = TestInjector.inject()
        fakeAppUpdateManager = component.fakeAppUpdateManager()
    }

    @Test
    fun testFlexibleUpdate_Completes() {

        fakeAppUpdateManager.partiallyAllowedUpdateType = AppUpdateType.FLEXIBLE
        fakeAppUpdateManager.setUpdateAvailable(2)

        launch(MainActivity::class.java)

        SystemClock.sleep(1000)

        // Validate that flexible update is prompted to the user
        assertTrue(fakeAppUpdateManager.isConfirmationDialogVisible)

        // Simulate user's and download behavior
        fakeAppUpdateManager.userAcceptsUpdate()
        fakeAppUpdateManager.downloadStarts()
        fakeAppUpdateManager.downloadCompletes()
        fakeAppUpdateManager.installCompletes()

        SystemClock.sleep(1000)
    }

    @Test
    fun testImmediateUpdate_Completes() {

        // Setup immediate update.
        fakeAppUpdateManager.partiallyAllowedUpdateType = AppUpdateType.IMMEDIATE
        fakeAppUpdateManager.setUpdateAvailable(2)

        launch(MainActivity::class.java)

        SystemClock.sleep(1000)

        // Validate that immediate update is prompted to the user
        assertTrue(fakeAppUpdateManager.isImmediateFlowVisible)

        // Simulate user's and download behavior
        fakeAppUpdateManager.userAcceptsUpdate()
        fakeAppUpdateManager.downloadStarts()
        fakeAppUpdateManager.downloadCompletes()

        // Validate that update is completed and app is restarted
        assertTrue(fakeAppUpdateManager.isInstallSplashScreenVisible)

        SystemClock.sleep(1000)
    }

    @Test
    fun testFlexibleUpdate_DownloadFails() {

        // Setup flexible update
        fakeAppUpdateManager.partiallyAllowedUpdateType = AppUpdateType.FLEXIBLE
        fakeAppUpdateManager.setUpdateAvailable(2)

        launch(MainActivity::class.java)

        SystemClock.sleep(1000)

        // Validate that flexible update is prompted to the user
        assertTrue(fakeAppUpdateManager.isConfirmationDialogVisible)

        // Simulate user's and download behavior
        fakeAppUpdateManager.userAcceptsUpdate()
        fakeAppUpdateManager.downloadStarts()
        fakeAppUpdateManager.downloadFails()

        SystemClock.sleep(1000)

        // Perform a click on the Snackbar to retry the update process
        onView(
            allOf(
                isDescendantOfA(instanceOf(Snackbar.SnackbarLayout::class.java)),
                instanceOf(AppCompatButton::class.java)
            )
        ).perform(ViewActions.click())

        // Validate that update is not completed and app is not restarted
        assertFalse(fakeAppUpdateManager.isInstallSplashScreenVisible)

        // Validate that Flexible update is prompted to the user again
        assertTrue(fakeAppUpdateManager.isConfirmationDialogVisible)

        SystemClock.sleep(1000)
    }
}
