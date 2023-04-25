package ru.mobileup.coinroad.system

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import ru.mobileup.coinroad.BuildConfig

class DeviceInfo(private val context: Context) {

    val androidDisplayedVersion: String
        get() = Build.VERSION.RELEASE

    val androidSDKVersion: Int
        get() = Build.VERSION.SDK_INT

    val buildDisplayedVersion: String
        get() = Build.DISPLAY

    val deviceBrand: String
        get() = Build.BRAND

    val deviceModel: String
        get() = Build.MODEL

    val versionCode: Int
        get() = BuildConfig.VERSION_CODE

    val versionName: String
        get() = BuildConfig.VERSION_NAME

    val deviceId: String
        @SuppressLint("HardwareIds")
        get() {
            return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }
}