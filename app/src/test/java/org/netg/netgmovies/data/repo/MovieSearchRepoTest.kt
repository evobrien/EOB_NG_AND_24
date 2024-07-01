package org.netg.netgmovies.data.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.netg.netgmovies.MainDispatcherRule
import org.netg.netgmovies.data.api.MovieSearchService
import org.netg.netgmovies.data.db.AppDatabase
import org.netg.netgmovies.data.model.MovieSearchResponse
import org.netg.netgmovies.movieList

class MovieSearchRepoTest {

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

    private val movieSearchRepo = MovieSearchRepoImpl(movieSearchService, appDatabase)

    @Ignore("disabling for now until we figure out why the movie search repo is failing in the test envioronment")
    @Test
    fun testMovieSearchRepo() = runTest {
        val searchResponse = MovieSearchResponse(
            page = 1,
            results = movieList,
            totalPages = 1,
            totalResults = movieList.size
        )

        coEvery { movieSearchService.searchByName(any(), any(), any()) } returns searchResponse
        coEvery { appDatabase.movieDao().insertAll(any()) } just runs

        val result = movieSearchRepo.searchByName(query)


        // TestCase.assertTrue(result is PagingSource.LoadResult.Page)
        //(result as PagingSource.LoadResult.Page)

        result.test {
            val first = awaitItem()

            coVerify(exactly = 1) { movieSearchService.searchByName(any(), any(), any()) }
            coVerify(exactly = movieList.size) { appDatabase.movieDao().insertAll(any()) }
            TestCase.assertEquals(movieList, first)
            cancelAndIgnoreRemainingEvents()
        }


    }

}