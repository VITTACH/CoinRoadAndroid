package ru.mobileup.coinroad.domain.usecase.billing

import kotlinx.coroutines.flow.first
import ru.mobileup.coinroad.subscription.BillingProvider

class LoadAdsPricesInteractor(
    private val billingProvider: BillingProvider
) {

    suspend fun load(fresh: Boolean = false) = billingProvider.getAdsRemovalPrices().first()

    fun observe() = billingProvider.getAdsRemovalPrices()
}