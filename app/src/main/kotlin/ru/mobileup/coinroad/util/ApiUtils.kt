package ru.mobileup.coinroad.util

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import ru.mobileup.coinroad.domain.exception.DeserializationException
import ru.mobileup.coinroad.domain.exception.NetworkException
import ru.mobileup.coinroad.domain.exception.NoServerResponseException
import ru.mobileup.coinroad.domain.exception.ServerErrorException
import java.io.IOException
import java.net.SocketTimeoutException

suspend inline fun <R> makeApiCall(crossinline block: suspend () -> R): R {
    return try {
        block()
    } catch (e: Exception) {
        val error = when (e) {
            is SerializationException -> DeserializationException

            is HttpException -> ServerErrorException(cause = e)

            is IOException -> if (e is SocketTimeoutException) {
                NoServerResponseException
            } else NetworkException(e)

            else -> e
        }
        throw error
    }
}