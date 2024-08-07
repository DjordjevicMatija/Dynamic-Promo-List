package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieResponse

interface MovieDataSource {
    suspend fun getMovies(): List<MovieResponse>
}