package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.util

import retrofit2.HttpException
import retrofit2.Response
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.NetworkResponse

object NetworkHelper {

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResponse<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let {
                    NetworkResponse.Success(it)
                } ?: NetworkResponse.Failure(NullPointerException("Response body is null"))
            } else {
                NetworkResponse.Failure(HttpException(response))
            }
        } catch (e: Exception) {
            NetworkResponse.Failure(e)
        }
    }
    
}
