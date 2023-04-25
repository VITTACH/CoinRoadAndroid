package ru.mobileup.coinroad.domain.common

import android.content.Context
import ru.mobileup.coinroad.R
import kotlin.time.Duration
import kotlin.time.minutes

/**
 * Interval between bars on [Graph]
 */
enum class TimeStep(val duration: Duration) {
    ONE_MINUTE(1.minutes),
    FIVE_MINUTES(5.minutes),
    TEN_MINUTES(10.minutes),
    FIFTEEN_MINUTES(15.minutes),
    THIRTY_MINUTES(30.minutes),
    SIXTY_MINUTES(60.minutes);

    fun toString(context: Context): String {
        return when (this) {
            ONE_MINUTE -> context.getString(R.string.min_01)
            FIVE_MINUTES -> context.getString(R.string.min_05)
            TEN_MINUTES -> context.getString(R.string.min_10)
            FIFTEEN_MINUTES -> context.getString(R.string.min_15)
            THIRTY_MINUTES -> context.getString(R.string.min_30)
            SIXTY_MINUTES -> context.getString(R.string.hour_1)
        }
    }

    companion object {
        val DEFAULT = FIVE_MINUTES
    }
}
