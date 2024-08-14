package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieListResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.VideoListResponse

interface MovieApiService {

    @GET("popular")
    suspend fun getMovies(): Response<MovieListResponse>

    @GET("{movie_id}/videos")
    suspend fun getVideos(@Path("movie_id") id: Int): Response<VideoListResponse>
}
