package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models

sealed class NetworkResponse<out T> {
    data class Success<out T>(val data: T) : NetworkResponse<T>()
    data class Failure(val error: Throwable) : NetworkResponse<Nothing>()
}