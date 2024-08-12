package rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieResponse

data class Movie(
    val id: Int?,
    val title: String?,
    val backdropPath: String?,
    val posterPath: String?,
    val voteAverage: Double?,
    val overview: String?
)

fun MovieResponse.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        backdropPath = backdropPath,
        posterPath = posterPath,
        voteAverage = voteAverage,
        overview = overview
    )
}