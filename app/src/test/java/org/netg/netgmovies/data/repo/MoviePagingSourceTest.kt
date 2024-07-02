package org.netg.netgmovies.data.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.netg.netgmovies.MainDispatcherRule
import org.netg.netgmovies.data.api.MovieSearchService
import org.netg.netgmovies.data.db.AppDatabase
import org.netg.netgmovies.data.model.Movie
import org.netg.netgmovies.data.model.MovieSearchResponse
import org.netg.netgmovies.movieList

class MoviePagingSourceTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainRule = MainDispatcherRule(UnconfinedTestDispatcher())

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val movieSearchService = mockk<MovieSearchService>()
    private val appDatabase = mockk<AppDatabase>()
    private val query = "a_random_query_text"

    private val pagingSource = MoviePagingSource(movieSearchService, query, appDatabase)

    @Test
    fun `test MoviePagingSource refresh`() = runTest {

        val searchResponse = MovieSearchResponse(
            page = 1,
            results = movieList,
            totalPages = 1,
            totalResults = movieList.size
        )

        coEvery { movieSearchService.searchByName(any(), any(), any()) } returns searchResponse
        coEvery { appDatabase.movieDao().insertAll(any()) } just runs

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 1,
                placeholdersEnabled = false
            )
        )

        coVerify(exactly = 1) { movieSearchService.searchByName(any(), any(), any()) }
        coVerify(exactly = movieList.size) { appDatabase.movieDao().insertAll(any()) }
        assertTrue(result is PagingSource.LoadResult.Page)
        (result as PagingSource.LoadResult.Page)

        assertEquals(movieList, result.data)
        assertEquals(result.nextKey, 2)
        assertEquals(result.prevKey, -1)

    }

    @Test
    fun `test appending paging source items`() = runTest {
        val appendList = listOf(
            Movie(
                id = 1, adult = false, genreIds = listOf(1, 2, 3),
                originalLanguage = "en", backdropPath = "",
                originalTitle = "", overview = "movie3 overview",
                posterPath = "", releaseDate = "1-1-2012", title = "movie3", video = false,
                popularity = 9.0, voteAverage = 9.0, voteCount = 100
            )
        )

        val appendSearchResponse = MovieSearchResponse(
            page = 2,
            results = appendList,
            totalPages = 2,
            totalResults = movieList.size + appendList.size
        )

        coEvery {
            movieSearchService.searchByName(
                any(),
                any(),
                any()
            )
        } returns appendSearchResponse
        coEvery { appDatabase.movieDao().insertAll(any()) } just runs

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 1,
                loadSize = 1,
                placeholdersEnabled = false
            )
        )

        coVerify(exactly = 1) { movieSearchService.searchByName(any(), any(), any()) }
        coVerify(exactly = appendList.size) { appDatabase.movieDao().insertAll(any()) }
        assertTrue(result is PagingSource.LoadResult.Page)
        (result as PagingSource.LoadResult.Page)

        assertEquals(appendList, result.data)
        assertEquals(3, result.nextKey) //page value +1
        assertEquals(null, result.prevKey)
    }

    @Test
    fun `verify exceptions in MoviePagingSource return a LoadResult Error`() = runTest {
        val message = "this is a message"
        coEvery { movieSearchService.searchByName(any(), any(), any()) } throws Exception(message)
        coEvery { appDatabase.movieDao().insertAll(any()) } just runs

        val res = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 1,
                placeholdersEnabled = false
            )
        )

        coVerify(exactly = 1) { movieSearchService.searchByName(any(), any(), any()) }
        coVerify(exactly = 0) { appDatabase.movieDao().insertAll(any()) }

        assertTrue(res is PagingSource.LoadResult.Error)
        assertEquals(message, (res as PagingSource.LoadResult.Error).throwable.message)

    }

    @Test(expected = CancellationException::class)
    fun `verify coroutine cancellation exceptions in MoviePagingSource are rethrown`() = runTest {
        coEvery {
            movieSearchService.searchByName(
                any(),
                any(),
                any()
            )
        } throws CancellationException()
        coEvery { appDatabase.movieDao().insertAll(any()) } just runs

        pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 1,
                placeholdersEnabled = false
            )
        )

        coVerify(exactly = 1) { movieSearchService.searchByName(any(), any(), any()) }
        coVerify(exactly = 0) { appDatabase.movieDao().insertAll(any()) }

    }


}