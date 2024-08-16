package rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities

data class MovieDTO(
    val id: Int?,
    val title: String?,
    val backdropPath: String?,
    val posterPath: String?,
    val voteAverage: Double?,
    val overview: String?
)
