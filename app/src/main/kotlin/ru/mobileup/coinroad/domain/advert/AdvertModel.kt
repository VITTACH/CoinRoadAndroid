package ru.mobileup.coinroad.domain.advert

import com.google.android.gms.ads.nativead.NativeAd

enum class Period { None, Month, Year }

enum class AdProvider { Google }

data class AdvertModel(
    val provider: AdProvider,
    val id: String? = null,
    val action: String?,
    val company: String?,
    val title: String? = null,
    val body: String?,
    val image: String?,
    val logo: String?,
    val clickUrl: String? = null,
    val impUrl: String? = null,
    val nativeAd: NativeAd? = null
)

fun NativeAd.toAdvertModel() = AdvertModel(
    provider = AdProvider.Google,
    action = callToAction?.toString(),
    company = advertiser?.toString(),
    image = images.firstOrNull()?.uri?.toString(),
    title = headline?.toString(),
    body = body?.toString(),
    logo = icon?.uri?.toString(),
    nativeAd = this
)
