package ru.mobileup.coinroad.util

import android.content.Context
import android.os.Build
import android.text.format.DateFormat
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.domain.common.TimeStep
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

private val DATE_FORMAT_LOCK = Any()

fun formatLocale(context: Context, time: Long, separator: String? = null): String {
    val dateSkeleton = "dd.MM.yy"
    val timeSkeleton = if (DateFormat.is24HourFormat(context)) "HH:mm" else "hh:mm a"
    val locale = context.resources.configuration.locale
    val datePattern: String
    val timePattern: String
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        datePattern = DateFormat.getBestDateTimePattern(locale, dateSkeleton)
        timePattern = DateFormat.getBestDateTimePattern(locale, timeSkeleton)
    } else {
        datePattern = dateSkeleton
        timePattern = timeSkeleton
    }
    return synchronized(DATE_FORMAT_LOCK) {
        with(SimpleDateFormat()) {
            if (separator != null) {
                applyPattern(timePattern + separator + datePattern)
            } else {
                applyPattern(timePattern)
            }
            dateFormatSymbols = DateFormatSymbols.getInstance(locale)
            format(time)
        }
    }
}

fun formatLocale(context: Context, date: Date, separator: String): String {
    return formatLocale(context, date.time, separator)
}

fun formatTime(interval: Int): String {
    val (time, suffix) = if (interval >= 60) {
        String.format("%.0f", interval / 60f) to "min"
    } else {
        interval to "sec"
    }
    return buildString {
        append(time)
        append(" ")
        append(suffix)
    }
}

fun Context.formatDiffTime(instant1: Instant, instant2: Instant = Clock.System.now()): String {
    val minutesDiff = instant1.until(instant2, DateTimeUnit.MINUTE)
    val hours = minutesDiff / 60
    val days = hours / 24
    return when {
        minutesDiff < 60 -> "$minutesDiff ${getString(R.string.minutes)}"
        hours < 24 -> "$hours ${getString(R.string.hourses)}"
        else -> "$days ${getString(R.string.days)}"
    }
}

fun Instant.roundDown(timeStep: TimeStep): Instant {
    val time = this.toLocalDateTime(TimeZone.UTC)
    val roundedMinute = when (timeStep) {
        TimeStep.ONE_MINUTE -> time.minute
        TimeStep.FIVE_MINUTES -> (time.minute / 5) * 5
        TimeStep.TEN_MINUTES -> (time.minute / 10) * 10
        TimeStep.FIFTEEN_MINUTES -> (time.minute / 15) * 15
        TimeStep.THIRTY_MINUTES -> (time.minute / 30) * 30
        TimeStep.SIXTY_MINUTES -> (time.minute / 60) * 60
    }
    return time.date.atTime(time.hour, roundedMinute)
        .toInstant(TimeZone.UTC)
}
