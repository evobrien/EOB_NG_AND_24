package org.netg.netgmovies.data.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.netg.netgmovies.MainDispatcherRule
import org.netg.netgmovies.data.db.AppDatabase
import org.netg.netgmovies.data.db.model.MovieEntity

class MovieDetailRepoTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainRule = MainDispatcherRule(UnconfinedTestDispatcher())

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val appDatabase = mockk<AppDatabase>()
    private val movieDetailRepo = MovieDetailRepoImpl(appDatabase)

    @Test
    fun `test happy path`() = runTest {
        val entity = MovieEntity(
            id = 1, releaseDate = "4-1-2001", title = "A movie",
            overview = "some overview text", backdropPath = "/abackdroppath.jpg",
            posterPath = "/aposterpath.jpg"
        )

        coEvery { appDatabase.movieDao().getMovieById(any()) } returns entity
        val result = movieDetailRepo.getMovieDetailsById(1)
        coVerify(exactly = 1) { appDatabase.movieDao().getMovieById(any()) }
        assertEquals(entity, result)
    }

    @Test(expected = Exception::class)
    fun `verify exceptions are thrown to the next level up`() = runTest {
        coEvery { appDatabase.movieDao().getMovieById(any()) } throws Exception()
        movieDetailRepo.getMovieDetailsById(1)
        coVerify(exactly = 1) { appDatabase.movieDao().getMovieById(any()) }
    }
}