package ru.mobileup.coinroad.data.storage.advert

import android.content.Context
import com.github.ajalt.timberkt.w
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_LEFT
import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.coinroad.BuildConfig
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.domain.advert.AdvertModel
import ru.mobileup.coinroad.domain.advert.toAdvertModel

class AdvertStorageImpl(private val context: Context) : AdvertStorage {

    private val adGoogleUnitId = if (BuildConfig.DEBUG) {
        context.getString(R.string.test_ad_google_unit_id)
    } else {
        context.getString(R.string.ad_google_unit_id)
    }

    private var advertState = MutableStateFlow<AdvertModel?>(null)

    override fun observeAdvert() = advertState

    override fun fetchAdvert(): AdvertModel? {
        loadGoogleAd()
        return advertState.value
    }

    private fun loadGoogleAd() {

        AdLoader.Builder(context, adGoogleUnitId)
            .forNativeAd { advertState.value = it.toAdvertModel() }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    w { "Failed Loading() Google Ad" }
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(ADCHOICES_TOP_LEFT)
                    .setReturnUrlsForImageAssets(true)
                    .build()
            )
            .build()
            .loadAds(AdRequest.Builder().build(), 5)
    }
}
