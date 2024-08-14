package rs.ac.bg.etf.dm200157d.dynamicpromolist.domain

import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.MovieList
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.Video

interface UseCase {
    suspend fun getMovies(): DataResult<MovieList>
    suspend fun getVideo(id: Int): DataResult<Video>
}