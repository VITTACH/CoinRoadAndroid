package ru.mobileup.coinroad.analytics.system

import ru.mobileup.coinroad.analytics.AnalyticMessage
import ru.mobileup.coinroad.domain.common.Graph

object ReportFirstLaunch : AnalyticMessage
object ReportMobileUpSiteClick : AnalyticMessage
object ReportAboutScreenShown : AnalyticMessage
data class ReportWorkManagerStatus(val graph: Graph) : AnalyticMessage