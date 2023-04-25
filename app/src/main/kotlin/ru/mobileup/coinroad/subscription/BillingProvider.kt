package ru.mobileup.coinroad.subscription

import android.app.Activity
import kotlinx.coroutines.flow.Flow
import ru.mobileup.coinroad.domain.advert.Period

interface BillingProvider {
    fun areSubscriptionsSupported(): Boolean
    fun purchaseMonthlyAdFreeRemoval(activity: Activity, previousProductId: String? = null)
    fun purchaseYearlyAdFreeRemoval(activity: Activity, previousProductId: String? = null)
    fun queryPurchases()
    fun queryPurchasesAsync()
    fun getAdsRemovalPrices(): Flow<Map<Product, String>>
    fun arePurchasesSupported(): Flow<Boolean>
    fun getPurchasedProduct(): Flow<Product>
    fun hasAdFreePurchase(): Flow<Boolean>
}

open class Product(val id: String, val period: Period)