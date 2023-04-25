package ru.mobileup.coinroad.data.storage.currency

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.Exchange


class InMemoryCurrencyPairStorage : CurrencyPairStorage {

    private val currencyPairMap = mutableMapOf<Exchange.Ids, MutableStateFlow<List<CurrencyPair>>>()

    override fun observeCurrencyPairs(exchangeId: Exchange.Ids): Flow<List<CurrencyPair>> {
        return getOrCreateFlow(exchangeId)
    }

    override suspend fun saveCurrencyPairs(
        exchangeId: Exchange.Ids,
        currencyPairs: List<CurrencyPair>
    ) {
        getOrCreateFlow(exchangeId).value = currencyPairs
    }

    @Synchronized
    private fun getOrCreateFlow(exchangeId: Exchange.Ids): MutableStateFlow<List<CurrencyPair>> {
        return currencyPairMap.getOrPut(exchangeId) {
            MutableStateFlow(emptyList())
        }
    }
}