package ru.mobileup.coinroad.domain.exception

sealed class ApplicationException(cause: Throwable? = null) : Exception(cause)

class NetworkException(cause: Throwable) : ApplicationException(cause)

class ServerErrorException(cause: Throwable? = null) : ApplicationException(cause)

object DeserializationException : ApplicationException()

object NoServerResponseException : ApplicationException()