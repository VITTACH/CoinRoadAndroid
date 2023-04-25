package ru.mobileup.coinroad.system

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

class ResourceHelper(private val context: Context) {

    fun getDrawable(@DrawableRes drawableRes: Int) =
        ContextCompat.getDrawable(context, drawableRes)

    fun getString(@StringRes stringRes: Int): String =
        context.getString(stringRes)

    fun getString(@StringRes stringRes: Int, vararg formatArgs: Any): String =
        context.getString(stringRes, *formatArgs)

    fun getQuantityString(@PluralsRes stringRes: Int, quantity: Int): String =
        context.resources.getQuantityString(stringRes, quantity)

    fun getQuantityString(@PluralsRes stringRes: Int, quantity: Int, vararg formatArgs: Any): String =
        context.resources.getQuantityString(stringRes, quantity, *formatArgs)

    fun getStringsList(@ArrayRes arrayRes: Int) =
        context.resources.getStringArray(arrayRes).toList()
}