package org.netg.netgmovies.ui.feature.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.netg.netgmovies.data.model.MovieSearchResponse
import org.netg.netgmovies.data.repo.MovieSearchRepo
import org.netg.netgmovies.di.Dispatchers
import org.netg.netgmovies.ui.feature.search.domain.SearchMovieUseCase
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val movieSearchRepo: MovieSearchRepo,
    private val searchMovieUseCase: SearchMovieUseCase,
    @Dispatchers.IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState: MutableStateFlow<StateData> =
        MutableStateFlow(value = StateData.Init())
    val uiState: StateFlow<StateData> get() = _uiState.asStateFlow()

    /*init{
        onEvent(searchEvent = SearchScreenEvent.SearchQuery(query = "Falling down"))
    }*/

    private var currentQuery: String = ""

    fun onEvent(searchEvent: SearchScreenEvent) {
        when (searchEvent) {
            is SearchScreenEvent.SearchQuery -> {
                _uiState.value = StateData.Loading()
                currentQuery = searchEvent.query
                getMovies(currentQuery)
            }

            is SearchScreenEvent.LoadNextPage -> getMovies(currentQuery)
        }
    }

    private fun getMovies(query: String) {

        val nextPage = if (_uiState.value is StateData.Success) {
            (uiState.value as StateData.Success).data.page + 1
        } else {
            1
        }
        //_uiState.value = StateData.Loading()
        viewModelScope.launch(ioDispatcher) {
            val result = searchMovieUseCase.invoke(query, nextPage)
            try {
                if (result.isSuccess) {
                    val new = result.getOrThrow()
                    if (_uiState.value is StateData.Success) {
                        val original = (_uiState.value as StateData.Success).data

                        _uiState.value = StateData.Success(
                            original.copy(
                                page = new.page,
                                results = original.results + new.results,
                                totalPages = new.totalPages,
                                totalResults = new.totalResults
                            )
                        )
                    } else {
                        _uiState.value = StateData.Success(new)
                    }
                } else {
                    _uiState.value = result.exceptionOrNull()
                        ?.let { StateData.Error(it.message ?: "Unknown Error") }
                        ?: StateData.Error("Unknown Error")
                }
            } catch (exception: Exception) {
                StateData.Error("Unknown Error")
            }

        }
    }

}

sealed class SearchScreenEvent {
    class SearchQuery(val query: String) : SearchScreenEvent()
    class LoadNextPage() : SearchScreenEvent()
}

sealed class StateData() {
    class Init(val initMessage: String = "Welcome to the app. Please enter your search in the search box") :
        StateData()

    class Success(val data: MovieSearchResponse) : StateData()
    class Error(val errorMessage: String) : StateData()
    class Loading(val loadingMessage: String = "Loading") : StateData()

}