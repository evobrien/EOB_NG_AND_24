package org.netg.netgmovies.ui.feature.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.netg.netgmovies.data.model.Movie
import org.netg.netgmovies.data.repo.MovieSearchRepo
import org.netg.netgmovies.di.Dispatchers
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val movieSearchRepo: MovieSearchRepo,
    @Dispatchers.IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState: MutableStateFlow<PagingData<Movie>> =
        MutableStateFlow(value = PagingData.empty())
    val uiState: StateFlow<PagingData<Movie>> get() = _uiState.asStateFlow()

    /*init{
        onEvent(searchEvent = SearchScreenEvent.SearchQuery(query = "Falling down"))
    }*/

    fun onEvent(searchEvent: SearchScreenEvent) {
        when (searchEvent) {
            is SearchScreenEvent.SearchQuery -> {
                getMovies(searchEvent.query)
            }
        }
    }

    private fun getMovies(query: String) {
        viewModelScope.launch(ioDispatcher) {
            movieSearchRepo.searchByName(query = query)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _uiState.value = pagingData
                }
        }
    }

}

sealed class SearchScreenEvent {
    class SearchQuery(val query: String) : SearchScreenEvent()
}