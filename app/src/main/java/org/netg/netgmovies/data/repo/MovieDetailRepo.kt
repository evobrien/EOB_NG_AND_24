package org.netg.netgmovies.data.repo

import org.netg.netgmovies.data.db.AppDatabase
import org.netg.netgmovies.data.db.model.MovieEntity
import javax.inject.Inject

interface MovieDetailRepo {
    suspend fun getMovieDetailsById(movieId: Int): MovieEntity
}

class MovieDetailRepoImpl @Inject constructor(private val appDatabase: AppDatabase) :
    MovieDetailRepo {
    override suspend fun getMovieDetailsById(movieId: Int): MovieEntity {
        return appDatabase.movieDao().getMovieById(movieId)
    }
}