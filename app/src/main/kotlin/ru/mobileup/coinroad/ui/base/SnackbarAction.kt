package ru.mobileup.coinroad.ui.base

data class SnackbarAction(
    val message: String,
    val actionTitle: String,
    val action: () -> Unit = { }
)