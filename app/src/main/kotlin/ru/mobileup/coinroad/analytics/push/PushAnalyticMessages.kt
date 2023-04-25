package ru.mobileup.coinroad.analytics.push

import ru.mobileup.coinroad.analytics.AnalyticMessage
import ru.mobileup.coinroad.domain.analytics.PushModel

data class ReportPushCreate(val pushModel: PushModel) : AnalyticMessage
data class ReportPushRemove(val pushModel: PushModel) : AnalyticMessage
data class ReportPushVisibility(val pushModel: PushModel, val isVisible: Boolean) : AnalyticMessage
data class ReportMinMaxVisibility(val pushModel: PushModel, val isVisible: Boolean) : AnalyticMessage