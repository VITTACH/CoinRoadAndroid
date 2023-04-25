package ru.mobileup.coinroad.util

import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.domain.common.Exchange


val Exchange.iconResource: Int
    get() = when (id) {
        Exchange.Ids.BINANCE -> R.drawable.ic_binance_dark
        Exchange.Ids.BITMEX -> R.drawable.ic_bitmex_dark
        Exchange.Ids.BITSTAMP -> R.drawable.ic_bitstamp_dark
        Exchange.Ids.HITBTC -> R.drawable.ic_hitbtc_dark
        Exchange.Ids.KICKEX -> R.drawable.ic_kickex_dark
        Exchange.Ids.KUCOIN -> R.drawable.ic_kucoin_dark
        Exchange.Ids.KRAKEN -> R.drawable.ic_kraken_dark
        Exchange.Ids.POLONIEX -> R.drawable.ic_poloniex_dark
        else -> R.drawable.ic_24_exchange_default
    }
