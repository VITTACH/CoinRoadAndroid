package ru.mobileup.coinroad.data.gateway.time

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone

class RealTimeGateway : TimeGateway {

    override val currentTime: Instant get() = Clock.System.now()

    override val timeZone: TimeZone get() = TimeZone.currentSystemDefault()
}