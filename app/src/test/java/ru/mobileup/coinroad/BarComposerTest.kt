package ru.mobileup.coinroad

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.mobileup.coinroad.domain.common.Bar
import ru.mobileup.coinroad.domain.common.BarComposer
import ru.mobileup.coinroad.domain.common.TimeStep
import ru.mobileup.coinroad.domain.common.Trade

class BarComposerTest {

    companion object {
        private val CURRENT_DATE = LocalDate(2020, Month.OCTOBER, 20)
        private val CURRENT_LOCAL_TIME = CURRENT_DATE.atTime(14, 50)       // "20 Oct 2020, 14:50"
        private val CURRENT_TIME = CURRENT_LOCAL_TIME.toInstant(TimeZone.UTC)
    }

    @Test
    fun `creates bars without gaps`() = runBlocking {
        val trades = listOf(
            // [14:00, 14:15)
            createTrade("14:02", 100),
            createTrade("14:03", 170),
            createTrade("14:07", 30),

            // [14:15, 14:30)
            createTrade("14:16", 30),
            createTrade("14:17", 170),
            createTrade("14:19", 10),
            createTrade("14:28", 100),

            // [14:30, 14:45)
            createTrade("14:31", 100)
        )
        val composer = BarComposer()

        val actual = composer.compose(trades, TimeStep.FIFTEEN_MINUTES, CURRENT_TIME)
        val expected = listOf(
            createBar("14:00", low = 30, high = 170, open = 100, close = 30),
            createBar("14:15", low = 10, high = 170, open = 30, close = 100),
            createBar("14:30", low = 100, high = 100, open = 100, close = 100)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `creates bars with filled gaps`() = runBlocking {
        val trades = listOf(
            // [14:00, 14:15)
            createTrade("14:02", 100),
            createTrade("14:03", 170),
            createTrade("14:07", 30),

            // [14:15, 14:30)
            // nothing

            // [14:30, 14:45)
            createTrade("14:31", 100)
        )
        val composer = BarComposer()

        val actual = composer.compose(trades, TimeStep.FIFTEEN_MINUTES, CURRENT_TIME)

        val expected = listOf(
            createBar("14:00", low = 30, high = 170, open = 100, close = 30),
            createBar("14:15", low = 30, high = 30, open = 30, close = 30), // filled gap
            createBar("14:30", low = 100, high = 100, open = 100, close = 100)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `creates bars for trades on borders of time frames`() = runBlocking {
        val trades = listOf(
            // [14:00, 14:15)
            createTrade("14:00", 100),
            createTrade("14:03", 170),
            createTrade("14:07", 30),

            // [14:15, 14:30)
            createTrade("14:15", 30),
            createTrade("14:17", 170),
            createTrade("14:19", 10),
            createTrade("14:28", 100),

            // [14:30, 14:45)
            createTrade("14:30", 100)
        )
        val composer = BarComposer()

        val actual = composer.compose(trades, TimeStep.FIFTEEN_MINUTES, CURRENT_TIME)

        val expected = listOf(
            createBar("14:00", low = 30, high = 170, open = 100, close = 30),
            createBar("14:15", low = 10, high = 170, open = 30, close = 100),
            createBar("14:30", low = 100, high = 100, open = 100, close = 100)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `drops bars in future`() = runBlocking {
        // current time is 14:50
        val trades = listOf(
            createTrade(
                "14:50",
                10
            ),   // drops it too, because an interval does not include end time
            createTrade("14:56", 10),
            createTrade("15:00", 10)
        )
        val composer = BarComposer()

        val actual = composer.compose(trades, TimeStep.ONE_MINUTE, CURRENT_TIME)

        val expected = emptyList<Bar>()
        assertEquals(expected, actual)
    }

    @Test
    fun `supports different time steps`() = runBlocking {
        val trades = listOf(
            // [14:00, 14:05)
            createTrade("14:02", 100),
            createTrade("14:03", 170),

            // [14:05, 14:10)
            createTrade("14:07", 30),

            // [14:10, 14:15)
            // nothing

            // [14:15, 14:20)
            createTrade("14:16", 30),
            createTrade("14:17", 170),
            createTrade("14:19", 10),

            // [14:20, 14:25)
            // nothing

            // [14:25, 14:30)
            createTrade("14:28", 100),

            // [14:30, 14:35)
            createTrade("14:31", 100)
        )
        val composer = BarComposer()

        val actual = composer.compose(trades, TimeStep.FIVE_MINUTES, CURRENT_TIME)

        val expected = listOf(
            createBar("14:00", low = 100, high = 170, open = 100, close = 170),
            createBar("14:05", low = 30, high = 30, open = 30, close = 30),
            createBar("14:10", low = 30, high = 30, open = 30, close = 30),
            createBar("14:15", low = 10, high = 170, open = 30, close = 10),
            createBar("14:20", low = 10, high = 10, open = 10, close = 10),
            createBar("14:25", low = 100, high = 100, open = 100, close = 100),
            createBar("14:30", low = 100, high = 100, open = 100, close = 100)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `creates empty bars for empty trades`() = runBlocking {
        val trades = emptyList<Trade>()
        val composer = BarComposer()

        val actual = composer.compose(trades, TimeStep.FIFTEEN_MINUTES, CURRENT_TIME)

        val expected = emptyList<Bar>()
        assertEquals(expected, actual)
    }

    @Test
    fun `creates single bar for single trade`() = runBlocking {
        val trades = listOf(
            // [14:00, 14:15)
            createTrade("14:02", 100)
        )
        val composer = BarComposer()

        val actual = composer.compose(trades, TimeStep.FIFTEEN_MINUTES, CURRENT_TIME)

        val expected = listOf(
            createBar("14:00", low = 100, high = 100, open = 100, close = 100)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `creates bars for trades one of that ends after a current time`() = runBlocking {
        val trades = listOf(
            // [14:00, 14:15)
            createTrade("14:02", 100),
            createTrade("14:03", 170),
            createTrade("14:07", 30),

            // [14:15, 14:30)
            createTrade("14:16", 30),
            createTrade("14:17", 170),
            createTrade("14:19", 10),
            createTrade("14:28", 100),

            // [14:30, 14:45)
            createTrade("14:31", 100),

            // [14:45, 15:00)   // ends after a current time (14:50)
            createTrade("14:48", 80)
        )
        val composer = BarComposer()

        val actual = composer.compose(trades, TimeStep.FIFTEEN_MINUTES, CURRENT_TIME)

        val expected = listOf(
            createBar("14:00", low = 30, high = 170, open = 100, close = 30),
            createBar("14:15", low = 10, high = 170, open = 30, close = 100),
            createBar("14:30", low = 100, high = 100, open = 100, close = 100),
            createBar("14:45", low = 80, high = 80, open = 80, close = 80)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `creates single bar for trades with first trade early than current time with time step`() = runBlocking {
        val trades = listOf(
            createTrade("14:45", 100),
            createTrade("14:47", 97),
            createTrade("14:49", 131)
        )
        val composer = BarComposer()

        val actual = composer.compose(trades, TimeStep.FIFTEEN_MINUTES, CURRENT_TIME)

        val expected = listOf(
            createBar("14:45", low = 97, high = 131, open = 100, close = 131)
        )
        assertEquals(expected, actual)
    }

    // Utils

    private fun createTrade(time: String, price: Int): Trade {
        return Trade(
            time = parseTime(time),
            price = price.toBigDecimal()
        )
    }

    private fun createBar(time: String, low: Int, high: Int, open: Int, close: Int): Bar {
        return Bar(
            startTime = parseTime(time),
            lowPrice = low.toBigDecimal(),
            highPrice = high.toBigDecimal(),
            openPrice = open.toBigDecimal(),
            closePrice = close.toBigDecimal()
        )
    }

    private fun parseTime(time: String): Instant {
        val timeParts = time.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()
        return CURRENT_DATE.atTime(hour, minute).toInstant(TimeZone.UTC)
    }
}