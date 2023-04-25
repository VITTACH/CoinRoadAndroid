package ru.mobileup.coinroad.subscription

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.OK
import com.android.billingclient.api.BillingClient.FeatureType.SUBSCRIPTIONS
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import me.aartikov.sesame.loading.simple.Loading.State
import me.aartikov.sesame.loading.simple.Loading.State.*
import timber.log.Timber.w

class GoogleBillingManager(val context: Context) : BillingManager, PurchasesUpdatedListener {

    interface SkuDetailsResponse {
        fun onSuccess(result: List<SkuDetails>)
        fun onError(e: Throwable)
    }

    class PlayServicesException(connectionResult: Int) :
        Exception("Google Play Services failure ($connectionResult)")

    class BillingStateException(@BillingClient.BillingResponseCode responseCode: Int) :
        Exception("Google Billing failure ($responseCode)")

    class SkuDetailsException(@BillingClient.BillingResponseCode responseCode: Int) :
        Exception("Google Billing failure ($responseCode)")

    object SubscriptionsNotSupportedException :
        Exception("Google Billing failure, Subscriptions feature is not supported")

    object PurchasesVerificationException :
        Exception("Google Billing failure, purchases are not verified")

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener(this)
            .build()

    private var serviceConnected = false

    override val purchasesSub = MutableStateFlow<State<List<Purchase>>>(Empty)

    override fun onPurchasesUpdated(billingResult: BillingResult, p1: MutableList<Purchase>?) {
        if (billingResult.responseCode == OK) queryPurchases()
    }

    override fun queryPurchases() = executeServiceRequest {
        w("Querying purchases")
        if (areSubscriptionsSupported()) {
            val result = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
            w("Queried purchases with response code ${result.responseCode}")
            if (result.responseCode == OK) {
                onQueryPurchasesFinished(result.purchasesList)
            } else {
                purchasesSub.value = Error(BillingStateException(result.responseCode))
            }
        } else {
            w("Subscription are not supported")
            purchasesSub.value = Error(SubscriptionsNotSupportedException)
        }
    }

    override fun initiatePurchase(
        activity: Activity,
        skuId: String,
        oldSku: String?,
        @BillingClient.SkuType skuType: String
    ) {
        w("Launching in-app purchase flow. Replace old SKU? %s", (oldSku != null))

        fetchSkuDetailsList(listOf(skuId), skuType, object : SkuDetailsResponse {
            override fun onSuccess(result: List<SkuDetails>) {
                val params = BillingFlowParams.newBuilder().setSkuDetails(result.first()).build()
                billingClient.launchBillingFlow(activity, params)
            }

            override fun onError(e: Throwable) {
                throw e
            }
        })
    }

    override fun areSubscriptionsSupported() =
        billingClient.isFeatureSupported(SUBSCRIPTIONS).responseCode == OK

    override fun getProductDetails(skuIds: List<String>, skuType: String) = callbackFlow {
        fetchSkuDetailsList(skuIds, skuType, object : SkuDetailsResponse {
            override fun onSuccess(result: List<SkuDetails>) {
                trySend(result)
            }

            override fun onError(e: Throwable) {
                throw e
            }
        })
        awaitClose {}
    }

    override fun queryPurchasesAsync(@BillingClient.SkuType skuType: String) {
        executeServiceRequest {
            billingClient.queryPurchaseHistoryAsync(skuType) { billingResult, purchasesList ->
                if (billingResult.responseCode == OK) {
                    w("query $purchasesList")
                }
            }
        }
    }

    private fun verifyServicesAndConnect(onSuccess: () -> Unit) {
        val connectionResult =
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)

        if (connectionResult == ConnectionResult.SUCCESS) {
            w("Google Play Services available. Setting up BillingClient")
            purchasesSub.value = Loading
            startBillingServiceConnection(onSuccess)
        } else {
            purchasesSub.value = Error(PlayServicesException(connectionResult))
        }
    }

    private fun startBillingServiceConnection(onSuccess: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                w("BillingClient setup finished. Response code: ${billingResult.responseCode}")

                if (billingResult.responseCode == OK) {
                    serviceConnected = true
                    onSuccess.invoke()
                } else {
                    purchasesSub.value = Error(BillingStateException(billingResult.responseCode))
                }
            }

            override fun onBillingServiceDisconnected() {
                w("BillingClient service disconnected")
                serviceConnected = false
            }
        })
    }

    private fun executeServiceRequest(request: () -> Unit) {
        if (serviceConnected) {
            request.invoke()
        } else {
            verifyServicesAndConnect(request)
        }
    }

    private fun onQueryPurchasesFinished(purchases: MutableList<Purchase>?) {
        w("Queried ${purchases?.size} purchases")
        if (purchases.isNullOrEmpty()) {
            onPurchasesVerificationFailure(PurchasesVerificationException)
        } else {
            onPurchasesVerified(purchases)
        }
    }

    private fun onPurchasesVerificationFailure(throwable: Throwable) {
        w("On purchases verification failure: ${throwable.message}")
        purchasesSub.value = (Error(throwable))
    }

    private fun onPurchasesVerified(purchases: MutableList<Purchase>) {
        w("On purchases verified, size = ${purchases.size}")
        purchasesSub.value = Data(purchases)
    }

    private fun fetchSkuDetailsList(
        skuIds: List<String>,
        skuType: String,
        callback: SkuDetailsResponse
    ) = executeServiceRequest {
        val params = SkuDetailsParams.newBuilder().setSkusList(skuIds).setType(skuType).build()

        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            when (billingResult.responseCode) {
                OK -> callback.onSuccess(skuDetailsList!!.toList())
                else -> callback.onError(SkuDetailsException(billingResult.responseCode))
            }
        }
    }
}