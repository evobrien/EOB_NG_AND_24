package org.netg.netgmovies.ui.feature.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.netg.netgmovies.di.Dispatchers
import org.netg.netgmovies.ui.feature.detail.domain.MovieDetailUseCase
import org.netg.netgmovies.ui.feature.detail.model.MovieDetail
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val movieDetailUseCase: MovieDetailUseCase,
    @Dispatchers.IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _detailFlow = MutableStateFlow<MovieState>(MovieState.Loading("Loading ...."))
    val detailFlow: StateFlow<MovieState> get() = _detailFlow.asStateFlow()

    fun onEvent(movieDetailEvent: MovieDetailEvent) {
        when (movieDetailEvent) {
            is MovieDetailEvent.LoadMovieDetail -> getMovieDetail(movieDetailEvent.movieId)
        }
    }

    private fun getMovieDetail(movieId: Int) {
        _detailFlow.value = MovieState.Loading("Loading ....")
        viewModelScope.launch(ioDispatcher) {
            try {
                val result = movieDetailUseCase.invoke(movieId)
                if (result.isSuccess) {
                    _detailFlow.value = MovieState.Success(result.getOrThrow())
                } else {
                    _detailFlow.value = MovieState.Failed("An unknown error occurred")
                }
            } catch (e: Exception) {
                _detailFlow.value = MovieState.Failed("An error occurred: ${e.message}")
            }
        }
    }

}

sealed class MovieDetailEvent {
    class LoadMovieDetail(val movieId: Int) : MovieDetailEvent()
}


sealed class MovieState {
    class Success(val movieDetail: MovieDetail) : MovieState()
    class Failed(val errorText: String) : MovieState()
    class Loading(val loadingText: String) : MovieState()
}