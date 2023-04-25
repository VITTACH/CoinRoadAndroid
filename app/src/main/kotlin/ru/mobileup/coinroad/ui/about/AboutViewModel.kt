package ru.mobileup.coinroad.ui.about

import ru.mobileup.coinroad.analytics.AnalyticsManager
import ru.mobileup.coinroad.analytics.system.ReportAboutScreenShown
import ru.mobileup.coinroad.analytics.system.ReportMobileUpSiteClick
import ru.mobileup.coinroad.navigation.system.OpenExternalUrl
import ru.mobileup.coinroad.ui.base.BaseViewModel
import java.net.URL


class AboutViewModel(
    private val analyticsManager: AnalyticsManager
) : BaseViewModel() {

    companion object {
        private const val MOBILE_UP_URL = "https://mobileup.ru/"
        const val LANDING_PAGE_URL = "https://mobileup.ru/coinroad.html"
    }

    override fun onActive() {
        super.onActive()
        analyticsManager.handleAnalyticMessage(ReportAboutScreenShown)
    }

    fun onMobileUpClicked() {
        analyticsManager.handleAnalyticMessage(ReportMobileUpSiteClick)
        navigate(OpenExternalUrl(URL(MOBILE_UP_URL)))
    }
}