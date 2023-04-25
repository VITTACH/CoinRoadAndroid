package ru.mobileup.coinroad.domain.advert

import ru.mobileup.coinroad.subscription.Product

data class AdvertData(
    val model: AdvertModel,
    val prices: Map<Product, String>?,
    val purchased: Product?
)