package ru.mobileup.coinroad.ui.advert

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.AdCardLayoutBinding
import ru.mobileup.coinroad.databinding.LayoutAdsRemovalBinding
import ru.mobileup.coinroad.domain.advert.AdProvider
import ru.mobileup.coinroad.domain.advert.AdvertData
import ru.mobileup.coinroad.domain.advert.Period
import ru.mobileup.coinroad.subscription.BillingProvider
import ru.mobileup.coinroad.subscription.BillingProviderImpl.AdFreeMonthly
import ru.mobileup.coinroad.subscription.BillingProviderImpl.AdFreeYearly
import ru.mobileup.coinroad.subscription.Product
import ru.mobileup.coinroad.util.ui.doOnClick


fun AdCardLayoutBinding.bindView(
    data: AdvertData?,
    onProductClicked: (Period, Product?) -> Unit
) = when (data?.model?.provider) {
    AdProvider.Google -> adGoogleLayout.apply {
        val radius = root.resources.getDimension(R.dimen.extra_small_gap)

        adSkeletonLayout.root.isVisible = false
        adNativeView.isVisible = true

        adCloseView.doOnClick { showAdsRemovalDialog(root.context, data, onProductClicked) }

        Glide.with(adImageView)
            .load(data.model.logo ?: data.model.image)
            .apply(RequestOptions().centerInside().transform(RoundedCorners(radius.toInt())))
            .into(adImageView)

        adBodyView.text = data.model.body
        adActionView.text = data.model.action
        adCompanyView.text = data.model.company
        adTitleView.text = data.model.title
        adTitleView.setLines(1)

        adNativeView.apply {
            data.model.nativeAd?.let { setNativeAd(it) }
            imageView = adImageView
            headlineView = adTitleView
            bodyView = adBodyView
            advertiserView = adCompanyView
            callToActionView = adActionView
        }

        adCard.setOnClickListener(null)
    }

    else -> adSkeletonLayout.apply {
        adCloseView.doOnClick { showAdsRemovalDialog(root.context, data, onProductClicked) }
    }
}

private fun showAdsRemovalDialog(
    context: Context,
    data: AdvertData?,
    onProductClicked: (Period, Product?) -> Unit
) = with(LayoutAdsRemovalBinding.inflate(LayoutInflater.from(context))) {

    data?.prices?.forEach { (product, price) ->
        when (product) {
            AdFreeMonthly -> btnMonthly.text = price
            AdFreeYearly -> btnYearly.text = price
        }
    }

    checkYearly.isVisible = data?.purchased == AdFreeYearly
    checkMonthly.isVisible = data?.purchased == AdFreeMonthly

    btnYearly.doOnClick { onProductClicked(Period.Year, data?.purchased) }
    btnMonthly.doOnClick { onProductClicked(Period.Month, data?.purchased) }

    MaterialAlertDialogBuilder(context)
        .setView(root)
        .setNegativeButton(R.string.cancel, null)
        .create()
        .show()
}

fun Activity.onPurchase(period: Period, purchased: Product?) {
    val billingProvider by inject<BillingProvider>()

    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(getString(R.string.purchase_url, purchased?.id, packageName))
    )
    when (purchased) {
        AdFreeMonthly -> when (period) {
            Period.Month -> startActivity(intent)
            Period.Year -> billingProvider.purchaseYearlyAdFreeRemoval(this, purchased.id)
        }
        AdFreeYearly -> when (period) {
            Period.Year -> startActivity(intent)
            Period.Month -> billingProvider.purchaseMonthlyAdFreeRemoval(this, purchased.id)
        }
        else -> when (period) {
            Period.Month -> billingProvider.purchaseMonthlyAdFreeRemoval(this)
            Period.Year -> billingProvider.purchaseYearlyAdFreeRemoval(this)
        }
    }
}