package ru.mobileup.coinroad.subscription

import android.app.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.mobileup.coinroad.domain.advert.Period
import ru.mobileup.coinroad.util.system.dataOrNull

class BillingProviderImpl(private val billingManager: BillingManager) : BillingProvider {

    companion object {
        private const val PRODUCT_AD_FREE_MONTHLY = "ru.mobileup.coinroad.subs.lite.month"
        private const val PRODUCT_AD_FREE_YEARLY = "ru.mobileup.coinroad.subs.lite.year"
    }

    object Unspecified : Product("unspecified", Period.None)
    object AdFreeMonthly : Product(PRODUCT_AD_FREE_MONTHLY, Period.Month)
    object AdFreeYearly : Product(PRODUCT_AD_FREE_YEARLY, Period.Year)

    private fun productFromSku(sku: String?): Product = when (sku) {
        PRODUCT_AD_FREE_MONTHLY -> AdFreeMonthly
        PRODUCT_AD_FREE_YEARLY -> AdFreeYearly
        else -> Unspecified
    }

    override fun queryPurchases() = billingManager.queryPurchases()

    override fun queryPurchasesAsync() = billingManager.queryPurchasesAsync()

    override fun areSubscriptionsSupported() = billingManager.areSubscriptionsSupported()

    override fun arePurchasesSupported() = billingManager.purchasesSub.map { it.dataOrNull != null }

    override fun purchaseMonthlyAdFreeRemoval(activity: Activity, previousProductId: String?) {
        billingManager.initiatePurchase(activity, PRODUCT_AD_FREE_MONTHLY, previousProductId)
    }

    override fun purchaseYearlyAdFreeRemoval(activity: Activity, previousProductId: String?) {
        billingManager.initiatePurchase(activity, PRODUCT_AD_FREE_YEARLY, previousProductId)
    }

    override fun hasAdFreePurchase(): Flow<Boolean> =
        hasAnyPurchases(PRODUCT_AD_FREE_MONTHLY, PRODUCT_AD_FREE_YEARLY)

    private fun hasAnyPurchases(vararg productIds: String) =
        billingManager.purchasesSub.map { state ->
            state.dataOrNull?.find { productIds.contains(it.skus.first()) } != null
        }

    override fun getPurchasedProduct() = billingManager.purchasesSub.map {
        productFromSku(it.dataOrNull?.firstOrNull()?.skus?.first())
    }

    override fun getAdsRemovalPrices(): Flow<Map<Product, String>> =
        billingManager.getProductDetails(listOf(PRODUCT_AD_FREE_MONTHLY, PRODUCT_AD_FREE_YEARLY))
            .map {
                it.associate { productFromSku(it.sku) to it.price }
            }
}