package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models

sealed class NetworkResponse<T: Any> {
    data class Success<T: Any>(val data: T) : NetworkResponse<T>()
    data class Failure<T: Any>(val error: Throwable) : NetworkResponse<T>()
}