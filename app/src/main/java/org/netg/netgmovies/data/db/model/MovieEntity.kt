package org.netg.netgmovies.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val releaseDate: String?,
    val title: String,
    val overview: String?,
    val backdropPath: String?,
    val posterPath: String?
)