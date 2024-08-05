package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.entities

import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.models.MovieResponse

data class Movie(
    val id: Long,
    val title: String,
    val backdropPath: String,
    val posterPath: String,
    val voteAverage: Double,
    val overview: String
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