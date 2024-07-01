package org.netg.netgmovies.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.netg.netgmovies.ui.feature.detail.DetailScreen
import org.netg.netgmovies.ui.feature.detail.viewmodel.DetailViewModel
import org.netg.netgmovies.ui.feature.search.SearchScreen
import org.netg.netgmovies.ui.feature.search.viewmodel.SearchViewModel

enum class AppScreen {
    Search, Detail
}

fun NavHostController.navigateToDetails(id: Int) {
    this.navigate(AppScreen.Detail.name + "/$id")
}

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreen.Search.name) {
        composable(route = AppScreen.Search.name) {
            val searchViewModel = hiltViewModel<SearchViewModel>()
            SearchScreen(
                viewModel = searchViewModel,
                navHostController = navController
            )
        }
        composable(route = AppScreen.Detail.name + "/{id}", arguments = listOf(navArgument("id") {
            type =
                NavType.IntType
        })) {
            val detailViewModel = hiltViewModel<DetailViewModel>()
            DetailScreen(
                viewModel = detailViewModel,
                navHostController = navController,
                id = it.arguments?.getInt("id", -1) ?: -1
            )
        }
    }
}