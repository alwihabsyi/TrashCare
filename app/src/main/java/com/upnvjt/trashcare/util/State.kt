package com.upnvjt.trashcare.util

sealed class State<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T): State<T>(data)
    class Error<T>(message: String): State<T>(message = message)
    class Loading<T>: State<T>()
}