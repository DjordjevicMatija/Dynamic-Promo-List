package rs.ac.bg.etf.dm200157d.dynamicpromolist.domain

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.repository.Repository
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.MovieList
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.Video
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.toMovie
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.toVideo
import javax.inject.Inject

class UseCaseImpl @Inject constructor(
    private val repository: Repository
) : UseCase {

    override suspend fun getMovies(): DataResult<MovieList> {
        return repository.getMovies().toDataResult { movieListResponse ->
            movieListResponse.results.map { it.toMovie() }
        }
    }

    override suspend fun getVideo(id: Int): DataResult<Video> {
        return repository.getVideos(id).toDataResult { videoListResponse ->
            videoListResponse.results.first {
                it.site == "YouTube" && it.type == "Trailer"
            }.toVideo()
        }
    }
}