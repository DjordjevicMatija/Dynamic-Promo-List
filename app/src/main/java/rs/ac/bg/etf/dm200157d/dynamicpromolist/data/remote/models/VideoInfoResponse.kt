package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models

import com.google.gson.annotations.SerializedName

data class VideoInfoResponse(

    @SerializedName("audio_url") var audioUrl: String? = null,
    @SerializedName("video_url") var videoUrl: String? = null

)