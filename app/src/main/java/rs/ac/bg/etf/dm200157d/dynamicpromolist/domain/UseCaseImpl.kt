package rs.ac.bg.etf.dm200157d.dynamicpromolist.domain

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.repository.Repository
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.MovieList
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.toMovie
import javax.inject.Inject

class UseCaseImpl @Inject constructor(
    private val repository: Repository
) : UseCase {

    override suspend fun getMovies(): DataResult<MovieList> {

        return repository.getMovies().toDataResult { movieListResponse ->
            movieListResponse.results.map { it.toMovie() }
        }
    }
}