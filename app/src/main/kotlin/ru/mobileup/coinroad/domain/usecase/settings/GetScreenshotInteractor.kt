package ru.mobileup.coinroad.domain.usecase.settings

import android.graphics.Bitmap
import ru.mobileup.coinroad.data.storage.screenshot.ScreenshotStorage

class GetScreenshotInteractor(
    private val screenshotStorage: ScreenshotStorage
) {

    fun execute(): Bitmap? {
        return screenshotStorage.getScreenshot()
    }
}