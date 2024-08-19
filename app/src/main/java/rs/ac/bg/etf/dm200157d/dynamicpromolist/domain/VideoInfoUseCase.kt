package rs.ac.bg.etf.dm200157d.dynamicpromolist.domain

import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.VideoInfo

interface VideoInfoUseCase {
    suspend fun getVideoInfo(key: String): DataResult<VideoInfo>
}