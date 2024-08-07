package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class VideoListResponse (
    val id: Long,

    @SerialName("results")
    val videos: List<VideoResponse>
)

@Serializable
data class VideoResponse (
    @SerialName("iso_639_1")
    val iso639_1: String,

    @SerialName("iso_3166_1")
    val iso3166_1: String,

    val name: String,

    val key: String,

    val site: String,

    val size: Long,

    val type: String,

    val official: Boolean,

    @SerialName("published_at")
    val publishedAt: String,

    val id: String
)

