package ru.mobileup.coinroad.domain.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import ru.mobileup.coinroad.util.roundDown

class BarComposer {

    private data class TradeTimeFrame(
        val startTime: Instant,
        val tradesSortedByTime: List<Trade>
    )

    suspend fun compose(
        trades: List<Trade>,
        timeStep: TimeStep,
        currentTime: Instant
    ): List<Bar> = withContext(Dispatchers.Default) {
        val timeFrames = createTimeFrames(trades, timeStep, currentTime)
        val nullableBars = timeFrames.map { createBar(it) }
        return@withContext fillGaps(nullableBars, timeStep)
    }

    private fun createTimeFrames(
        trades: List<Trade>,
        timeStep: TimeStep,
        currentTime: Instant
    ): List<TradeTimeFrame> {
        if (trades.isEmpty()) {
            return emptyList()
        }

        val sortedTrades = trades.sortedBy { it.time }
        val timeFrames = mutableListOf<TradeTimeFrame>()
        var currentFrameStartTime = sortedTrades.first().time.roundDown(timeStep)
        var lastTradeIndex: Int? = null

        while (currentFrameStartTime < currentTime && lastTradeIndex != sortedTrades.lastIndex) {
            val tradesInCurrentTimeFrame = mutableListOf<Trade>()
            val startIndex = lastTradeIndex?.let { it + 1 } ?: 0
            for (index in startIndex..sortedTrades.lastIndex) {
                val trade = sortedTrades[index]
                if (trade.time >= currentFrameStartTime + timeStep.duration) {
                    break
                }

                tradesInCurrentTimeFrame.add(trade)
                lastTradeIndex = index
            }

            timeFrames.add(
                TradeTimeFrame(currentFrameStartTime, tradesInCurrentTimeFrame)
            )
            currentFrameStartTime += timeStep.duration
        }

        return timeFrames
    }

    private fun fillGaps(nullableBars: List<Bar?>, timeStep: TimeStep): List<Bar> {
        val bars = mutableListOf<Bar>()
        var previousBar: Bar? = null

        for (nullableBar in nullableBars) {
            val bar = nullableBar
                ?: previousBar?.let { createBarForGap(it, timeStep) }
                ?: continue
            bars.add(bar)
            previousBar = bar
        }

        return bars
    }

    private fun createBar(timeFrame: TradeTimeFrame): Bar? {
        val trades = timeFrame.tradesSortedByTime
        if (trades.isEmpty()) {
            return null
        }

        return Bar(
            startTime = timeFrame.startTime,
            lowPrice = trades.minOf { it.price },
            highPrice = trades.maxOf { it.price },
            openPrice = trades.first().price,
            closePrice = trades.last().price
        )
    }

    private fun createBarForGap(previousBar: Bar, timeStep: TimeStep): Bar {
        return Bar(
            startTime = previousBar.startTime + timeStep.duration,
            lowPrice = previousBar.closePrice,
            highPrice = previousBar.closePrice,
            openPrice = previousBar.closePrice,
            closePrice = previousBar.closePrice
        )
    }
}