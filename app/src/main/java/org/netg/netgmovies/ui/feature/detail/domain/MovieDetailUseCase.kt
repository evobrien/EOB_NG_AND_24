package org.netg.netgmovies.ui.feature.detail.domain

import dagger.hilt.android.scopes.ViewModelScoped
import org.netg.netgmovies.data.db.model.MovieEntity
import org.netg.netgmovies.data.repo.MovieDetailRepo
import org.netg.netgmovies.ui.feature.detail.model.MovieDetail
import javax.inject.Inject

@ViewModelScoped
class MovieDetailUseCase @Inject constructor(private val movieDetailRepo: MovieDetailRepo) {
    suspend fun invoke(movieId: Int): Result<MovieDetail> {
        return Result.success(movieDetailRepo.getMovieDetailsById(movieId).toMovieDetail())
    }
}

fun MovieEntity.toMovieDetail(): MovieDetail {
    return MovieDetail(
        id = this.id,
        releaseDate = this.releaseDate,
        title = this.title,
        overview = this.overview,
        backdropPath = this.backdropPath,
        posterPath = this.posterPath
    )
}