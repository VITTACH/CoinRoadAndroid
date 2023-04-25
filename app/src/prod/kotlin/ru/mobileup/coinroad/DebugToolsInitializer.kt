package ru.mobileup.coinroad

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import okhttp3.Interceptor

object DebugToolsInitializer {
    var interceptor: Interceptor? = null
        private set

    var chuckerCollector: ChuckerCollector? = null
        private set

    fun initialize(context: Context) {
    }
}