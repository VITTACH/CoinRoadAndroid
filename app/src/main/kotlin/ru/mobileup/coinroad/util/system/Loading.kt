package ru.mobileup.coinroad.util.system

import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.state

val <T : Any> Loading<T>.dataOrNull: T? get() = (state as? Loading.State.Data)?.data

val <T : Any> Loading.State<T>.dataOrNull: T? get() = (this as? Loading.State.Data)?.data
