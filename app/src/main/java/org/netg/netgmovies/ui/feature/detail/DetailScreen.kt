package org.netg.netgmovies.ui.feature.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import org.netg.netgmovies.ui.feature.common.compose.DefaultAppBar
import org.netg.netgmovies.ui.feature.common.compose.ErrorScreen
import org.netg.netgmovies.ui.feature.common.compose.LoadingProgress
import org.netg.netgmovies.ui.feature.common.compose.Screen
import org.netg.netgmovies.ui.feature.detail.model.MovieDetail
import org.netg.netgmovies.ui.feature.detail.viewmodel.DetailViewModel
import org.netg.netgmovies.ui.feature.detail.viewmodel.MovieDetailEvent
import org.netg.netgmovies.ui.feature.detail.viewmodel.MovieState

@Composable
fun DetailScreen(viewModel: DetailViewModel, navHostController: NavHostController, id: Int) {


    val result = viewModel.detailFlow.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = "key") {
        viewModel.onEvent(movieDetailEvent = MovieDetailEvent.LoadMovieDetail(id))
    }

    Screen(
        appbar = { DefaultAppBar(navController = navHostController) },
        content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                when (result.value) {
                    is MovieState.Loading -> {
                        LoadingProgress()
                    }

                    is MovieState.Failed -> ErrorScreen((result.value as MovieState.Failed).errorText)
                    is MovieState.Success -> DetailPage((result.value as MovieState.Success).movieDetail)
                }
            }
        })

}

@Composable
fun DetailPage(movieDetail: MovieDetail) {
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(8.dp)) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = movieDetail.title,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = movieDetail.releaseDate ?: "No release date available",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = movieDetail.overview ?: "No overview available",
            style = MaterialTheme.typography.bodyMedium
        )
        SubcomposeAsyncImage(
            modifier = Modifier.fillMaxWidth(),
            model = "https://image.tmdb.org/t/p/w500${movieDetail.posterPath}",
            loading = {
                CircularProgressIndicator()
            },
            contentDescription = "movie image",
            contentScale = ContentScale.Crop
        )
    }

}
