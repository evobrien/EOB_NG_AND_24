package org.netg.netgmovies.ui.feature.detail.model

data class MovieDetail(
    val id: Int,
    val releaseDate: String?,
    val title: String,
    val overview: String?,
    val backdropPath: String?,
    val posterPath: String?
)

