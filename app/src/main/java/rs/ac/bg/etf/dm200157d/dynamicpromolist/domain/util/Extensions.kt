package rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.util

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.VideoResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.MovieDTO
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.Video
import rs.ac.bg.etf.dm200157d.mdjlibrary.entities.Movie

fun MovieResponse.toMovie(imagePath: String): MovieDTO {
    return MovieDTO(
        id = id,
        title = title,
        backdropPath = imagePath + backdropPath,
        posterPath = imagePath + posterPath,
        voteAverage = voteAverage,
        overview = overview
    )
}

fun VideoResponse.toVideo(): Video {
    return Video(
        key = key,
        site = site,
        type = type
    )
}

fun MovieDTO.toLibMovie(): Movie{
    return Movie(
        id = id,
        title = title,
        backdropPath = backdropPath,
        posterPath = posterPath,
        voteAverage = voteAverage,
        overview = overview
    )
}