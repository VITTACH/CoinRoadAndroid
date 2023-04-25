package ru.mobileup.coinroad.data.storage.advert

import kotlinx.coroutines.flow.Flow
import ru.mobileup.coinroad.domain.advert.AdvertModel

interface AdvertStorage {

    fun fetchAdvert(): AdvertModel?
    fun observeAdvert(): Flow<AdvertModel?>
}