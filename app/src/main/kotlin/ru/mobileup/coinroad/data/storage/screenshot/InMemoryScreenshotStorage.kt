package ru.mobileup.coinroad.data.storage.screenshot

import android.graphics.Bitmap

class InMemoryScreenshotStorage : ScreenshotStorage {

    private var screenshot: Bitmap? = null

    override fun getScreenshot() = screenshot

    override fun saveScreenshot(screenshot: Bitmap?) {
        this.screenshot = screenshot
    }
}