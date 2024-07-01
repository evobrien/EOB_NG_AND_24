package org.netg.netgmovies.ui.feature.search.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.netg.netgmovies.data.model.Movie
import org.netg.netgmovies.data.repo.MovieSearchRepo
import javax.inject.Inject

interface SearchMovieUseCase {
    fun invoke(query: String): Flow<PagingData<Movie>>
}

class SearchMovieUseCaseImpl @Inject constructor(private val movieSearchRepo: MovieSearchRepo) :
    SearchMovieUseCase {
    override fun invoke(query: String): Flow<PagingData<Movie>> {
        return movieSearchRepo.searchByName(query)
    }
}