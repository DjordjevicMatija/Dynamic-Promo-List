package rs.ac.bg.etf.dm200157d.dynamicpromolist.domain

import android.content.Context
import rs.ac.bg.etf.dm200157d.R
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.repository.Repository
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.MovieDTOList
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.Video
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.util.toMovie
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.util.toVideo
import javax.inject.Inject

class UseCaseImpl @Inject constructor(
    private val repository: Repository,
    private val context: Context
) : UseCase {

    override suspend fun getMovies(): DataResult<MovieDTOList> {
        return repository.getMovies().toDataResult { movieListResponse ->
            movieListResponse.results.map { it.toMovie(context.getString(R.string.base_image_url)) }
        }
    }

    override suspend fun getVideo(id: Int): DataResult<Video> {
        return repository.getVideos(id).toDataResult { videoListResponse ->
            videoListResponse.results.find {
                it.site == "YouTube" && it.type == "Trailer"
            }?.toVideo()?:Video(null, null, null)
        }
    }
}