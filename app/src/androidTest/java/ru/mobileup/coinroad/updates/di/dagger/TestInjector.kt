package ru.mobileup.coinroad.updates.di.dagger

import androidx.test.platform.app.InstrumentationRegistry
import ru.mobileup.coinroad.App

object TestInjector {
    fun inject(): TestUpdatesApplicationComponent {
        val application = InstrumentationRegistry.getInstrumentation()
            .targetContext
            .applicationContext as App

        return DaggerTestUpdatesApplicationComponent
            .factory()
            .create(application)
            .also { it.inject(application) } as TestUpdatesApplicationComponent
    }
}

