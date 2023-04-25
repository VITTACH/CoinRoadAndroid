package ru.mobileup.coinroad.analytics.performers

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import ru.mobileup.coinroad.analytics.AnalyticEvent

class FirebaseAnalyticPerformer(context: Context) : AnalyticsPerformer {
    private var firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun perform(event: AnalyticEvent) {
        val bundle = Bundle()
        bundle.putString("UUID", event.deviceId)

        when (event.label) {
            is String -> bundle.putString(event.action, event.label)
            is Int -> bundle.putInt(event.action, event.label)
            else -> bundle.putString(event.action, event.label.toString())
        }

        firebaseAnalytics.logEvent(event.category, bundle)
    }
}