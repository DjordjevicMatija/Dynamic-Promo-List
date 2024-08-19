package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.VideoInfoResponse

interface VideoInfoApiService {

    @GET("get_video_info")
    suspend fun getVideoInfo(@Query("key") key: String): Response<VideoInfoResponse>
}