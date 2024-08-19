package rs.ac.bg.etf.dm200157d.dynamicpromolist.domain

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.repository.Repository
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.VideoInfo
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.util.toVideoInfo
import javax.inject.Inject

class VideoInfoUseCaseImpl @Inject constructor(
    private val repository: Repository
) : VideoInfoUseCase {
    override suspend fun getVideoInfo(key: String): DataResult<VideoInfo> {
        return repository.getVideoInfo(key).toDataResult { it.toVideoInfo() }
    }
}