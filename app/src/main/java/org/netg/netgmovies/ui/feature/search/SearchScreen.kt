package org.netg.netgmovies.ui.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.SubcomposeAsyncImage
import org.netg.netgmovies.data.model.Movie
import org.netg.netgmovies.ui.feature.common.compose.DefaultAppBar
import org.netg.netgmovies.ui.feature.common.compose.ErrorScreen
import org.netg.netgmovies.ui.feature.common.compose.LoadingProgress
import org.netg.netgmovies.ui.feature.common.compose.Screen
import org.netg.netgmovies.ui.feature.search.viewmodel.SearchScreenEvent
import org.netg.netgmovies.ui.feature.search.viewmodel.SearchViewModel
import org.netg.netgmovies.ui.navigation.navigateToDetails

@Composable
fun SearchScreen(viewModel: SearchViewModel, navHostController: NavHostController) {

    val result = viewModel.uiState.collectAsLazyPagingItems()

    Screen(
        appbar = {
            Column {
                DefaultAppBar(navController = navHostController)
                SearchSection(onSearch = { viewModel.onEvent(SearchScreenEvent.SearchQuery(it)) })
            }
        },
        content = {
            when (result.loadState.refresh) {
                is LoadState.Loading -> {
                    LoadingProgress()
                }

                is LoadState.Error -> {
                    ErrorScreen()
                }

                else -> {
                    if (result.itemCount <= 0) {
                        ErrorScreen("No results available")
                    } else {
                        EndlessMovieList(movies = result, navigate = {
                            navHostController.navigateToDetails(it)
                        })
                    }
                }
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSection(onSearch: (String) -> Unit) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    val searchHistory = remember { mutableStateListOf("") }

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .padding(10.dp)
    ) {
        SearchBar(modifier = Modifier.fillMaxWidth(),
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = {
                if (!searchHistory.contains(it)) {
                    searchHistory.add(it)
                }
                onSearch(it)
                active = false
            },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text(text = "Search for movie") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (active) {
                    Icon(
                        modifier = Modifier.clickable {
                            if (searchText.isNotEmpty()) {
                                searchText = ""
                            } else {
                                active = false
                            }
                        },
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }) {

            searchHistory.forEach {
                if (it.isNotEmpty()) {
                    Row(modifier = Modifier.padding(all = 14.dp)) {
                        Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(modifier = Modifier.clickable { searchText = it }, text = it)
                    }
                }
            }

            Divider()
            Text(
                modifier = Modifier
                    .padding(all = 14.dp)
                    .fillMaxWidth()
                    .clickable {
                        searchHistory.clear()
                    },
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                text = "Clear history"
            )

        }
    }

}

@Composable
fun EndlessMovieList(movies: LazyPagingItems<Movie>, navigate: (id: Int) -> Unit) {

    val lazyListState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxWidth()
            .padding(4.dp), state = lazyListState
    ) {
        items(count = movies.itemCount) { index ->
            val item = movies[index]
            item?.let {
                if (it.backdropPath != null) {
                    MovieCard(navigate, it)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MovieCard(
    navigate: (id: Int) -> Unit,
    item: Movie
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        onClick = { navigate(item.id) }
    ) {
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .padding(12.dp)
        ) {
            Text(text = item.title, style = MaterialTheme.typography.titleLarge)
            Text(
                text = item.releaseDate,
                style = MaterialTheme.typography.titleMedium
            )
            item.backdropPath?.let {
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = "https://image.tmdb.org/t/p/w500$it",
                    loading = {
                        CircularProgressIndicator()
                    },
                    contentDescription = "Movie image",
                    contentScale = ContentScale.Crop
                )
            }
        }

    }
}

/*@Composable
fun SearchPage() {
    SearchSection(null)
}

@Preview
@Composable
fun PreviewSearchBar() = SearchSection()
*/
