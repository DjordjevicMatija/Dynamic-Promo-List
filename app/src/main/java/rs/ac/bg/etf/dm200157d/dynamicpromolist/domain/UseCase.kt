package rs.ac.bg.etf.dm200157d.dynamicpromolist.domain

import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.MovieDTOList
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.Video

interface UseCase {
    suspend fun getMovies(): DataResult<MovieDTOList>
    suspend fun getVideo(id: Int): DataResult<Video>
}