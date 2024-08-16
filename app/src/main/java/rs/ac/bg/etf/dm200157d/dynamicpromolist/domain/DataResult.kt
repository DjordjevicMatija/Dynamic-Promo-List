package rs.ac.bg.etf.dm200157d.dynamicpromolist.domain

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.NetworkResponse

sealed class DataResult<T : Any> {
    data class Success<T : Any>(val data: T) : DataResult<T>()
    data class Failure<T : Any>(val error: Exception) : DataResult<T>()
}

fun <T : Any, R : Any> NetworkResponse<T>.toDataResult(transform: (T) -> R): DataResult<R> {
    return when (this) {
        is NetworkResponse.Success -> DataResult.Success(transform(data))
        is NetworkResponse.Failure -> DataResult.Failure(error)
    }
}