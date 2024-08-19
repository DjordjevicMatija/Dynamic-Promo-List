package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.NetworkResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.VideoInfoResponse

interface VideoInfoDataSource {
    suspend fun getVideoInfo(key: String): NetworkResponse<VideoInfoResponse>
}