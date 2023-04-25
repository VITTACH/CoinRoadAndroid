package ru.mobileup.coinroad.util

import androidx.work.*
import ru.mobileup.coinroad.system.NotificationWorkManager
import java.util.concurrent.TimeUnit

private const val UNIQUE_WORK_ID = "GRAPHS_WORK"
private const val WORK_TAG = "GRAPHS_TAG"

fun WorkManager.startWork() {
    cancelAllWork()
    val workRequest = OneTimeWorkRequestBuilder<NotificationWorkManager>()
        .setBackoffCriteria(
            BackoffPolicy.LINEAR,
            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        )
        .addTag(WORK_TAG)
        .build()

    enqueueUniqueWork(UNIQUE_WORK_ID, ExistingWorkPolicy.KEEP, workRequest)
}

fun WorkManager.isRunning(): Boolean {
    return try {
        var running = false
        getWorkInfosByTag(WORK_TAG).get().forEach {
            running = it.state == WorkInfo.State.RUNNING
        }
        running
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
