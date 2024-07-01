package org.netg.netgmovies.ui.feature.search.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import org.netg.netgmovies.data.repo.MovieSearchRepo
import org.netg.netgmovies.data.repo.MovieSearchRepoImpl
import org.netg.netgmovies.ui.feature.search.domain.SearchMovieUseCase
import org.netg.netgmovies.ui.feature.search.domain.SearchMovieUseCaseImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class SearchModule {

    @Binds
    abstract fun bindMovieSearchRepo(movieSearchRepoImpl: MovieSearchRepoImpl): MovieSearchRepo

    @Binds
    abstract fun bindSearchMovieUseCase(searchMovieUseCaseImpl: SearchMovieUseCaseImpl): SearchMovieUseCase
}