package org.netg.netgmovies

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.testing.asPagingSourceFactory
import kotlinx.coroutines.flow.Flow
import org.netg.netgmovies.data.model.Movie
import org.netg.netgmovies.data.model.MovieSearchResponse
import org.netg.netgmovies.data.repo.MovieSearchRepo

val movieList: List<Movie> = listOf(
    Movie(
        id = 1, adult = false, genreIds = listOf(1),
        originalLanguage = "en", backdropPath = "",
        originalTitle = "", overview = "movie1 overview",
        posterPath = "", releaseDate = "1-1-91", title = "movie1", video = false,
        popularity = 1.0, voteAverage = 1.0, voteCount = 10
    ),
    Movie(
        id = 1, adult = false, genreIds = listOf(1, 2),
        originalLanguage = "en", backdropPath = "",
        originalTitle = "", overview = "movie2 overview",
        posterPath = "", releaseDate = "1-1-91", title = "movie2", video = false,
        popularity = 9.0, voteAverage = 9.0, voteCount = 100
    )
)

val movieSearchResponse =
    MovieSearchResponse(page = 1, totalPages = 1, totalResults = 2, results = movieList)

class FakeSearchRepoImpl : MovieSearchRepo {
    override fun searchByName(query: String): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PREFETCH_DISTANCE),
            pagingSourceFactory = movieList.asPagingSourceFactory()
        ).flow
    }

    companion object {
        const val PAGE_SIZE = 20
        const val PREFETCH_DISTANCE = 1
    }
}