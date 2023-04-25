package ru.mobileup.coinroad.domain.usecase.currency

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.mobileup.coinroad.data.gateway.currency.CurrencyGatewayProvider
import ru.mobileup.coinroad.data.storage.currency.CurrencyPairStorage
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.Exchange

class LoadCurrencyPairsInteractor(
    private val gatewayProvider: CurrencyGatewayProvider,
    private val storage: CurrencyPairStorage
) {

    suspend fun execute(exchangeId: Exchange.Ids, fresh: Boolean): List<CurrencyPair> {
        val gateway = gatewayProvider.getCurrencyGateway(exchangeId)
        if (!fresh) {
            val cached = storage.observeCurrencyPairs(exchangeId).first()
            if (cached.isNotEmpty()) return cached
        }
        val currencyPairs = gateway.getCurrencyPairs()
        storage.saveCurrencyPairs(exchangeId, currencyPairs)
        return currencyPairs
    }

    fun observe(exchangeId: Exchange.Ids): Flow<List<CurrencyPair>> {
        return storage.observeCurrencyPairs(exchangeId)
    }
}