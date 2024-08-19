package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.repository

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieListResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.NetworkResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.VideoInfoResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.VideoListResponse

interface Repository {
    suspend fun getMovies(): NetworkResponse<MovieListResponse>
    suspend fun getVideos(id: Int): NetworkResponse<VideoListResponse>
    suspend fun getVideoInfo(key: String): NetworkResponse<VideoInfoResponse>
}