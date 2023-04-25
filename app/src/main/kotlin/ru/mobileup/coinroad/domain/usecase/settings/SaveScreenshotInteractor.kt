package ru.mobileup.coinroad.domain.usecase.settings

import android.graphics.Bitmap
import ru.mobileup.coinroad.data.storage.screenshot.ScreenshotStorage

class SaveScreenshotInteractor(
    private val screenshotStorage: ScreenshotStorage
) {

    fun execute(screenshot: Bitmap?) {
        screenshotStorage.saveScreenshot(screenshot)
    }
}