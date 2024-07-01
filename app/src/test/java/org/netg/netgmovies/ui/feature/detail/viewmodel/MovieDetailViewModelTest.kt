package org.netg.netgmovies.ui.feature.detail.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.netg.netgmovies.MainDispatcherRule
import org.netg.netgmovies.ui.feature.detail.domain.MovieDetailUseCase
import org.netg.netgmovies.ui.feature.detail.model.MovieDetail

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainRule = MainDispatcherRule(UnconfinedTestDispatcher())

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val movieDetailUseCase = mockk<MovieDetailUseCase>()

    private val viewModel = DetailViewModel(movieDetailUseCase, UnconfinedTestDispatcher())

    companion object {
        const val LOADING_TEXT = "Loading ...."
        const val EXCEPTION_TEXT = "this is a failure from exception"
        const val EXCEPTION_RESULT_TEXT = "An error occurred: this is a failure from exception"
        const val RESULT_FAILURE_TEXT = "An unknown error occurred"
    }

    private val movieDetail = MovieDetail(
        id = 1, releaseDate = "4-1-2001", title = "movie1", overview = "some overview text",
        backdropPath = "/abackdroppath.jpg", posterPath = "/aposeterpath.jpg"
    )

    @Test
    fun `test Success Event`() = runTest {

        coEvery { movieDetailUseCase.invoke(any()) } returns Result.success(movieDetail)

        val secondResult = MovieState.Success(movieDetail)
        val first = MovieState.Loading(LOADING_TEXT)

        viewModel.detailFlow.test {
            val firstItem = awaitItem()
            assertTrue(firstItem is MovieState.Loading)
            assertTrue(first.loadingText == (firstItem as MovieState.Loading).loadingText)
            viewModel.onEvent(MovieDetailEvent.LoadMovieDetail(movieId = 151))
            val second = awaitItem()
            assertTrue(second is MovieState.Loading)
            val third = awaitItem()
            assertTrue(third is MovieState.Success)
            assertEquals(secondResult.movieDetail, (third as MovieState.Success).movieDetail)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test exception in use is caught is returned as a Failed state`() = runTest {

        coEvery { movieDetailUseCase.invoke(any()) } throws Exception(EXCEPTION_TEXT)

        val first = MovieState.Loading(LOADING_TEXT)

        viewModel.detailFlow.test {
            val firstItem = awaitItem()
            assertTrue(firstItem is MovieState.Loading)
            assertTrue(first.loadingText == (firstItem as MovieState.Loading).loadingText)
            viewModel.onEvent(MovieDetailEvent.LoadMovieDetail(movieId = 151))
            val second = awaitItem()
            assertTrue(second is MovieState.Loading)
            val third = awaitItem()
            assertTrue(third is MovieState.Failed)
            assertEquals(EXCEPTION_RESULT_TEXT, (third as MovieState.Failed).errorText)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test failure from use case is returned as a Failed state`() = runTest {

        coEvery { movieDetailUseCase.invoke(any()) } returns Result.failure(Exception())
        val first = MovieState.Loading(LOADING_TEXT)

        viewModel.detailFlow.test {
            val firstItem = awaitItem()
            assertTrue(firstItem is MovieState.Loading)
            assertTrue(first.loadingText == (firstItem as MovieState.Loading).loadingText)
            viewModel.onEvent(MovieDetailEvent.LoadMovieDetail(movieId = 151))
            val second = awaitItem()
            assertTrue(second is MovieState.Loading)
            val third = awaitItem()
            assertTrue(third is MovieState.Failed)
            assertEquals(RESULT_FAILURE_TEXT, (third as MovieState.Failed).errorText)
            cancelAndIgnoreRemainingEvents()
        }
    }


}