package ru.mobileup.coinroad.domain.common

import java.io.Serializable

data class DeepLink(
    val id: String,
    val packageName: String
) : Serializable