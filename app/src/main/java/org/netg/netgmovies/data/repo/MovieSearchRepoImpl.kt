package org.netg.netgmovies.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.Flow
import org.netg.netgmovies.data.api.MovieSearchService
import org.netg.netgmovies.data.db.AppDatabase
import org.netg.netgmovies.data.model.Movie
import org.netg.netgmovies.data.model.MovieSearchResponse
import org.netg.netgmovies.data.model.toMovieEntity
import java.util.concurrent.CancellationException
import javax.inject.Inject


interface MovieSearchRepo {
    fun searchByName(query: String): Flow<PagingData<Movie>>
}

class MovieSearchRepoImpl @Inject constructor(
    private val movieSearchService: MovieSearchService,
    private val appDatabase: AppDatabase
) : MovieSearchRepo {
    override fun searchByName(query: String): Flow<PagingData<Movie>> {

        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PREFETCH_DISTANCE),
            pagingSourceFactory = {
                MoviePagingSource(movieSearchService, query, appDatabase)
            }
        ).flow
    }

    companion object {
        const val PAGE_SIZE = 20
        const val PREFETCH_DISTANCE = 1
    }
}

class MoviePagingSource(
    private val movieSearchService: MovieSearchService,
    private val query: String,
    private val appDatabase: AppDatabase,
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val currentPage = params.key ?: 1
            val movies = movieSearchService.searchByName(query, currentPage)
            saveToDb(movies, appDatabase)
            LoadResult.Page(
                data = movies.results,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (movies.results.isEmpty()) null else movies.page + 1
            )
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition
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

}