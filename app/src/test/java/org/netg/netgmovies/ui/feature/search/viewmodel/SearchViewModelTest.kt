package  org.netg.netgmovies.ui.feature.search.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.netg.netgmovies.MainDispatcherRule
import org.netg.netgmovies.asList
import org.netg.netgmovies.data.api.MovieSearchService
import org.netg.netgmovies.data.db.AppDatabase
import org.netg.netgmovies.data.repo.MovieSearchRepo
import org.netg.netgmovies.data.repo.MovieSearchRepoImpl
import org.netg.netgmovies.movieList
import org.netg.netgmovies.movieSearchResponse

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainRule = MainDispatcherRule(UnconfinedTestDispatcher())

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val movieSearchService = mockk<MovieSearchService>(relaxed = true)
    private val appDatabase = mockk<AppDatabase>(relaxed = true)
    private val mockRepo: MovieSearchRepo = mockk<MovieSearchRepo>()

    @Before
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    fun `Should be initialized to empty`() = runTest {
        val data = PagingData.from(movieList)
        coEvery { mockRepo.searchByName(any()) } coAnswers { flowOf(data) }

        val searchViewModel = SearchViewModel(mockRepo, UnconfinedTestDispatcher())

        searchViewModel.uiState.test {
            val initialState = awaitItem()
            val list = initialState.asList()
            assertTrue(list.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `Test Successful data returned `() = runTest {

        //PagingSource.LoadResult<Int,Movie>
        coEvery { mockRepo.searchByName(any()) } coAnswers { flowOf(PagingData.from(movieList)) }

        val searchViewModel = SearchViewModel(mockRepo, UnconfinedTestDispatcher())
        searchViewModel.onEvent(SearchScreenEvent.SearchQuery("test"))

        searchViewModel.uiState.test {
            val populatedPageData = awaitItem()
            val moviesResult = populatedPageData.asList()
            assertEquals(movieList, moviesResult)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Ignore("disable until we figure out how to mock errors for page data in the view model")
    @Test
    fun `test load errors`() = runTest {

        coEvery { movieSearchService.searchByName(any(), any()) } returns movieSearchResponse
        coEvery { appDatabase.movieDao().insertAll(any()) } just runs
        val movieSearchRepo = MovieSearchRepoImpl(movieSearchService, appDatabase)

        val searchViewModel = SearchViewModel(movieSearchRepo, UnconfinedTestDispatcher())
        searchViewModel.onEvent(SearchScreenEvent.SearchQuery("test"))

        searchViewModel.uiState.test {
            val populatedPageData = awaitItem()
            TestCase.assertNotNull(populatedPageData)
            val items = cancelAndConsumeRemainingEvents()
            assertEquals(null, items)

        }

    }
}