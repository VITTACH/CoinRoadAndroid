package ru.mobileup.coinroad.util.system

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip

fun <T, R> zip(
    vararg flows: Flow<T>,
    transform: suspend (List<T>) -> R = { it as R }
): Flow<R> = when (flows.size) {
    0 -> error("No flows")
    1 -> flows[0].map { transform(listOf(it)) }
    2 -> flows[0].zip(flows[1]) { a, b -> transform(listOf(a, b)) }
    else -> {
        var accFlow = flows[0].zip(flows[1]) { a, b -> listOf(a, b) }
        for (i in 2 until flows.size) {
            accFlow = accFlow.zip(flows[i]) { list, it -> list + it }
        }
        accFlow.map(transform)
    }
}