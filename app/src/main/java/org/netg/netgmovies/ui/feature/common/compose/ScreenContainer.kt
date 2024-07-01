package org.netg.netgmovies.ui.feature.common.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.netg.netgmovies.R

@Composable
fun Screen(appbar: @Composable () -> Unit, content: @Composable () -> Unit?) {

    Scaffold(topBar = { appbar() },
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                color = MaterialTheme.colorScheme.background
            ) {
                content()
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(id = R.string.app_name)
            )
        },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        Icons.Filled.ArrowBack, contentDescription = stringResource(
                            id = R.string.desc_back
                        )
                    )
                }
            } else {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Filled.Menu, contentDescription = stringResource(
                            id = R.string.desc_home
                        )
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primaryContainer)

    )
}

@Composable
fun LoadingProgress() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
    }

}

@Composable
fun ErrorScreen(message: String = "An error has occurred. Please try again later") {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message)
    }
}