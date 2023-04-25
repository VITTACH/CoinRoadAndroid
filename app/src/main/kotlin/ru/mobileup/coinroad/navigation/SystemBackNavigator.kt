package ru.mobileup.coinroad.navigation

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import ru.mobileup.coinroad.R
import java.util.*

class SystemBackNavigator {

    companion object {
        private const val TRY_TO_QUIT_INTERVAL_MS = 2000L
    }

    private var isTriedToQuit = false

    private var activities = mutableSetOf<FragmentActivity>()

    fun attach(activity: FragmentActivity) {
        activities.add(activity)
    }

    fun detach(activity: FragmentActivity) {
        activities.remove(activity)
    }

    fun closeApp(force: Boolean = false) {
        if (activities.isEmpty()) {
            throw NullPointerException("SystemBackNavigator require activity!")
        } else {
            if (isTriedToQuit || force) {
                activities.last().finish()
            } else {
                showFinishMessage(activities.last())
            }
            startBackPressedTimer()
        }
    }

    private fun showFinishMessage(context: Context) {
        isTriedToQuit = true
        Toast.makeText(
            context,
            context.getString(R.string.quit_toast_msg),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun startBackPressedTimer() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                isTriedToQuit = false
            }
        }, TRY_TO_QUIT_INTERVAL_MS)
    }
}