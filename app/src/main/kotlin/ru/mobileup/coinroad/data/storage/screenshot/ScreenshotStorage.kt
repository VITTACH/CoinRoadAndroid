package ru.mobileup.coinroad.data.storage.screenshot

import android.graphics.Bitmap

interface ScreenshotStorage {

    fun getScreenshot(): Bitmap?

    fun saveScreenshot(screenshot: Bitmap?)
}