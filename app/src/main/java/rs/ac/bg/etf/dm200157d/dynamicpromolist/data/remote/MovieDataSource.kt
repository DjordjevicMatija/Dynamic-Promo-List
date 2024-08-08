package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieListResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.NetworkResponse

interface MovieDataSource {
    suspend fun getMovies(): NetworkResponse<MovieListResponse>
}