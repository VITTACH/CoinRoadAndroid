package ru.mobileup.coinroad.data.storage.currency

import kotlinx.coroutines.flow.Flow
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.Exchange

interface CurrencyPairStorage {

    fun observeCurrencyPairs(exchangeId: Exchange.Ids): Flow<List<CurrencyPair>>

    suspend fun saveCurrencyPairs(exchangeId: Exchange.Ids, currencyPairs: List<CurrencyPair>)
}