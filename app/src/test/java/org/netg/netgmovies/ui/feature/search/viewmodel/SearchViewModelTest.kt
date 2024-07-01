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
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.netg.netgmovies.FakeSearchRepoImpl
import org.netg.netgmovies.MainDispatcherRule
import org.netg.netgmovies.data.api.MovieSearchService
import org.netg.netgmovies.data.db.AppDatabase
import org.netg.netgmovies.data.model.Movie
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
    private val movieSearchRepo: MovieSearchRepo =
        MovieSearchRepoImpl(movieSearchService, appDatabase)


    @Before
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Ignore("disabled for now - issue with the paging source")
    @Test
    fun `Should be initialized to empty`() = runTest {
        val searchViewModel = SearchViewModel(FakeSearchRepoImpl(), UnconfinedTestDispatcher())
        val emptyStateFlow: MutableStateFlow<PagingData<Movie>> =
            MutableStateFlow(value = PagingData.empty())
        assertEquals(emptyStateFlow.value, searchViewModel.uiState.value)
    }


    @Ignore("disabled for now - issue with the paging source")
    @Test
    fun `Test flow states`() = runTest {
        coEvery { movieSearchService.searchByName(any(), any()) } returns movieSearchResponse
        coEvery { appDatabase.movieDao().insertAll(any()) } just runs

        val searchViewModel = SearchViewModel(FakeSearchRepoImpl(), UnconfinedTestDispatcher())

        searchViewModel.uiState.test {
            searchViewModel.onEvent(SearchScreenEvent.SearchQuery("test"))
            val item = awaitItem()
            assertEquals(movieList, item)
            awaitComplete()
        }
    }
}

