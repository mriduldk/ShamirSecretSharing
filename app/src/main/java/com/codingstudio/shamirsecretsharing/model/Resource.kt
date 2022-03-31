package com.codingstudio.shamirsecretsharing.model

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val status: Int? = null
) {
    class Success<T>(data: T): Resource<T>(data = data)
    class Error<T>(errorMessage: String, data: T? = null): Resource<T>(data = data, message = errorMessage)
    class Error2<T>(status: Int?, data: T? = null): Resource<T>(data = data, status = status)
    class Loading<T>: Resource<T>()
}