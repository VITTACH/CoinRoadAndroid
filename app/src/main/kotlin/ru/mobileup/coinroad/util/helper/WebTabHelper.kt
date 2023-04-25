package ru.mobileup.coinroad.util.helper

import android.app.Activity
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.FragmentActivity
import java.net.URL

class WebTabHelper {

    private var activities = mutableSetOf<Activity>()

    fun attach(activity: FragmentActivity) {
        activities.add(activity)
    }

    fun detach(activity: FragmentActivity) {
        activities.remove(activity)
    }

    fun openUrl(url: URL) {
        if (activities.isEmpty()) {
            throw IllegalStateException("WebTabHelper is not attached to activity")
        } else {
            CustomTabsIntent.Builder().build()
                .launchUrl(activities.last(), Uri.parse(url.toString()))
        }
    }
}
