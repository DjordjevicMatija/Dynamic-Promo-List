package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.repositories

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.models.MovieResponse

interface MovieDataSource {
    suspend fun getMovies(): List<MovieResponse>
}