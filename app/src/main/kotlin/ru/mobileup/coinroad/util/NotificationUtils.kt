package ru.mobileup.coinroad.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi


@RequiresApi(Build.VERSION_CODES.O)
fun createNotificationChannel(
    notificationManager: NotificationManager,
    channelId: String,
    channelName: String,
    channelDescription: String,
): String {
    NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
        lightColor = Color.GREEN
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        description = channelDescription
        setSound(null, null)
        notificationManager.createNotificationChannel(this)
    }
    return channelId
}