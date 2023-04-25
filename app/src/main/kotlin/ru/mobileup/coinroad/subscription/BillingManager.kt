package ru.mobileup.coinroad.subscription

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.SkuType.SUBS
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import me.aartikov.sesame.loading.simple.Loading.State

interface BillingManager {

    val purchasesSub: MutableStateFlow<State<List<Purchase>>>

    fun areSubscriptionsSupported(): Boolean

    fun queryPurchases()

    fun queryPurchasesAsync(@BillingClient.SkuType skuType: String = SUBS)

    fun getProductDetails(
        skuIds: List<String>,
        @BillingClient.SkuType skuType: String = SUBS
    ): Flow<List<SkuDetails>>

    fun initiatePurchase(
        activity: Activity,
        skuId: String,
        oldSku: String? = null,
        @BillingClient.SkuType skuType: String = SUBS
    )
}