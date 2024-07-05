package org.netg.netgmovies.data.repo

import org.netg.netgmovies.data.api.MovieSearchService
import org.netg.netgmovies.data.db.AppDatabase
import org.netg.netgmovies.data.model.MovieSearchResponse
import org.netg.netgmovies.data.model.toMovieEntity
import javax.inject.Inject


interface MovieSearchRepo {
    suspend fun searchByName(query: String, page: Int): MovieSearchResponse
}

class MovieSearchRepoImpl @Inject constructor(
    private val movieSearchService: MovieSearchService,
    private val appDatabase: AppDatabase
) : MovieSearchRepo {
    override suspend fun searchByName(query: String, page: Int): MovieSearchResponse {

        val searchResponse = movieSearchService.searchByName(query, page)
        saveToDb(searchResponse, appDatabase)
        return searchResponse
    }

    private suspend fun saveToDb(
        movieSearchResponse: MovieSearchResponse,
        appDatabase: AppDatabase
    ) {
        movieSearchResponse.results.forEach {
            val entity = it.toMovieEntity()
            appDatabase.movieDao().insertAll(entity)
        }
    }
    companion object {
        const val PAGE_SIZE = 20
        const val PREFETCH_DISTANCE = 1
    }
}
