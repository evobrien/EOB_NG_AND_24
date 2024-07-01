package org.netg.netgmovies.ui.feature.detail.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import org.netg.netgmovies.data.repo.MovieDetailRepo
import org.netg.netgmovies.data.repo.MovieDetailRepoImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class DetailModule {

    @Binds
    abstract fun bindsMovieDetailRepo(movieDetailRepoImpl: MovieDetailRepoImpl): MovieDetailRepo

}