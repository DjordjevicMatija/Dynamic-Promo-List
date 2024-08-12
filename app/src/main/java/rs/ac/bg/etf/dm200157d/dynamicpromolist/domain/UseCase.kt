package rs.ac.bg.etf.dm200157d.dynamicpromolist.domain

import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.MovieList

interface UseCase {
    suspend fun getMovies(): DataResult<MovieList>
}