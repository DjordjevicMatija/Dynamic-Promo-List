package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.entities

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.models.VideoResponse

data class Video(
    val key: String,
    val site: String,
    val type: String
)

fun VideoResponse.toVideo(): Video {
    return Video(
        key = key,
        site = site,
        type = type
    )
}