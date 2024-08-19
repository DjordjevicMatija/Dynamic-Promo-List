package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.NetworkResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.VideoInfoResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.util.NetworkHelper
import javax.inject.Inject

class VideoInfoDataSourceImpl @Inject constructor(
    private val videoInfoService: VideoInfoApiService
) : VideoInfoDataSource {

    override suspend fun getVideoInfo(key: String): NetworkResponse<VideoInfoResponse> {
        return NetworkHelper.safeApiCall { videoInfoService.getVideoInfo(key) }
    }
}