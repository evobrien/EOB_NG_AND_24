package org.netg.netgmovies.ui.feature.search.domain

import org.netg.netgmovies.data.model.MovieSearchResponse
import org.netg.netgmovies.data.repo.MovieSearchRepo
import javax.inject.Inject

interface SearchMovieUseCase {
    suspend fun invoke(query: String, page: Int): Result<MovieSearchResponse>
}

class SearchMovieUseCaseImpl @Inject constructor(private val movieSearchRepo: MovieSearchRepo) :
    SearchMovieUseCase {
    override suspend fun invoke(query: String, page: Int): Result<MovieSearchResponse> {
        return runCatching {
            movieSearchRepo.searchByName(query, page)
        }
    }

}
