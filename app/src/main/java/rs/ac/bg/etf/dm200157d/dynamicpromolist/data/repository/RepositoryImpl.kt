package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.repository

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.MovieDataSource
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieListResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.NetworkResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.VideoListResponse
import javax.inject.Inject
import javax.inject.Singleton

class RepositoryImpl @Inject constructor(
    private val movieDataSource: MovieDataSource
) : Repository {

    override suspend fun getMovies(): NetworkResponse<MovieListResponse> {
        return movieDataSource.getMovies()
    }

    override suspend fun getVideos(id: Int): NetworkResponse<VideoListResponse> {
        return movieDataSource.getVideos(id)
    }
}