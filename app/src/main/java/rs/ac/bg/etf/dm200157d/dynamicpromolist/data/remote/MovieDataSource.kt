package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieListResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.NetworkResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.VideoListResponse

interface MovieDataSource {
    suspend fun getMovies(): NetworkResponse<MovieListResponse>
    suspend fun getVideos(id: Int): NetworkResponse<VideoListResponse>
}