package org.netg.netgmovies.ui.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import org.netg.netgmovies.data.model.Movie
import org.netg.netgmovies.ui.feature.common.compose.ErrorScreen
import org.netg.netgmovies.ui.feature.common.compose.LoadingProgress
import org.netg.netgmovies.ui.feature.common.compose.Screen
import org.netg.netgmovies.ui.feature.search.viewmodel.SearchScreenEvent
import org.netg.netgmovies.ui.feature.search.viewmodel.SearchViewModel
import org.netg.netgmovies.ui.feature.search.viewmodel.StateData
import org.netg.netgmovies.ui.navigation.navigateToDetails

@Composable
fun SearchScreen(viewModel: SearchViewModel, navHostController: NavHostController) {

    val result = viewModel.uiState.collectAsStateWithLifecycle()

    Screen(
        appbar = {
            Column {
                SearchSection(onSearch = { viewModel.onEvent(SearchScreenEvent.SearchQuery(it)) })
            }
        },
        content = {
            when (result.value) {
                is StateData.Loading -> {
                    LoadingProgress()
                }

                is StateData.Error -> {
                    ErrorScreen()
                }

                is StateData.Init -> {
                    ErrorScreen((result.value as StateData.Init).initMessage)
                }

                else -> {
                    val response = (result.value as StateData.Success).data
                    if (response.results.isEmpty()) {
                        ErrorScreen("No results available")
                    } else {
                        EndlessMovieList(movies = response.results, navigate = {
                            navHostController.navigateToDetails(it)
                        }, getMoreData = { viewModel.onEvent(SearchScreenEvent.LoadNextPage()) })
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
    val searchHistory = rememberMutableStateListOf("")

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
fun EndlessMovieList(movies: List<Movie>, navigate: (id: Int) -> Unit, getMoreData: () -> Unit) {

    val lazyListState = rememberLazyListState()

    // observe list scrolling
    val reachedBottom: Boolean by remember {
        derivedStateOf {
            val lastVisibleItem = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index != 0 && (lastVisibleItem?.index
                ?: 0) <= lazyListState.layoutInfo.totalItemsCount - 1
        }
    }

    // load more if scrolled to bottom
    LaunchedEffect(reachedBottom) {
        if (reachedBottom) getMoreData()
    }

    LazyColumn(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxWidth()
            .padding(4.dp), state = lazyListState
    ) {
        items(count = movies.size) { index ->
            val item = movies[index]
            item.let {
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
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    },
                    contentDescription = "Movie image",
                    contentScale = ContentScale.Crop
                )
            }
        }

    }
}

@Composable
fun <T : Any> rememberMutableStateListOf(vararg elements: T): SnapshotStateList<T> {
    return rememberSaveable(saver = snapshotStateListSaver()) {
        elements.toList().toMutableStateList()
    }
}

private fun <T : Any> snapshotStateListSaver() = listSaver<SnapshotStateList<T>, T>(
    save = { stateList -> stateList.toList() },
    restore = { it.toMutableStateList() },
)

/*@Composable
fun SearchPage() {
    SearchSection(null)
}

@Preview
@Composable
fun PreviewSearchBar() = SearchSection()
*/
