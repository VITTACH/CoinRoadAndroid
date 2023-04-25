package ru.mobileup.coinroad.analytics.performers

import com.amplitude.api.Amplitude
import org.json.JSONObject
import ru.mobileup.coinroad.BuildConfig
import ru.mobileup.coinroad.analytics.AnalyticEvent

class AmplitudeAnalyticPerformer : AnalyticsPerformer {

    override fun perform(event: AnalyticEvent) {

        val payload = JSONObject()
            .put("UUID", event.deviceId)
            .apply {
                if (event.label is Map<*, *>) {
                    event.label.forEach { (key, value) ->
                        put(if (key == "id") event.action else key as String, value)
                    }
                } else {
                    put(event.action, event.label.toString())
                }
            }

        Amplitude
            .getInstance(BuildConfig.AMPLITUDE_INSTANCE_NAME)
            .setDeviceId(event.deviceId)
            .logEvent(event.category, payload)
    }
}