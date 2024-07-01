package org.netg.netgmovies.ui.feature.detail.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.netg.netgmovies.MainDispatcherRule
import org.netg.netgmovies.data.db.model.MovieEntity
import org.netg.netgmovies.data.repo.MovieDetailRepo
import org.netg.netgmovies.ui.feature.detail.model.MovieDetail

class MovieDetailUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainRule = MainDispatcherRule(UnconfinedTestDispatcher())

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val movieDetailRepo = mockk<MovieDetailRepo>()

    private val movieDetailUseCase = MovieDetailUseCase(movieDetailRepo)

    @Test(expected = Exception::class)
    fun `Exceptions are throw to the top level`() = runTest {
        coEvery { movieDetailRepo.getMovieDetailsById(any()) } throws Exception()
        movieDetailUseCase.invoke(1)
        coVerify(exactly = 1) { movieDetailRepo.getMovieDetailsById(any()) }
    }

    @Test
    fun `Valid results are correctly converted from entity to domain object`() = runTest {
        val entity = MovieEntity(
            id = 1, releaseDate = "4-1-2001", title = "A movie",
            overview = "some overview text", backdropPath = "/abackdroppath.jpg",
            posterPath = "/aposterpath.jpg"
        )

        val movieDetail = MovieDetail(
            id = 1, releaseDate = "4-1-2001", title = "A movie",
            overview = "some overview text", backdropPath = "/abackdroppath.jpg",
            posterPath = "/aposterpath.jpg"
        )

        coEvery { movieDetailRepo.getMovieDetailsById(any()) } returns entity

        val result = movieDetailUseCase.invoke(1)
        assertTrue(result.isSuccess)
        val detailObject = result.getOrNull()
        assertEquals(movieDetail, detailObject)
    }

}