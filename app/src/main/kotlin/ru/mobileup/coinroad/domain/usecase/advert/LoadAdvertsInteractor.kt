package ru.mobileup.coinroad.domain.usecase.advert

import ru.mobileup.coinroad.data.storage.advert.AdvertStorage

class LoadAdvertsInteractor(private val accountStorage: AdvertStorage) {

    fun load(fresh: Boolean = false) = accountStorage.fetchAdvert()

    fun observe() = accountStorage.observeAdvert()
}