package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.repositories

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.models.MovieListResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.models.VideoListResponse

interface MovieApiService {

    @GET("popular")
    suspend fun getMovies(): Call<MovieListResponse>

    @GET("{movie_id}/videos")
    suspend fun getVideos(@Path("movie_id") id: Long): Call<VideoListResponse>
}
