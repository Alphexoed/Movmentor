package dev.alphexo.movmentor.train.tabs.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NorthEast
import androidx.compose.material.icons.rounded.SouthEast
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.alphexo.movmentor.R
import dev.alphexo.movmentor.train.endpoints.Stations


@Composable
@Preview(showSystemUi = true, showBackground = true)
fun SearchTab() {
    val stations = remember { Stations() }
    val coroutineScope = rememberCoroutineScope()
    /*val stationsList = remember {
        coroutineScope.launch {
            coroutineScope.async(Dispatchers.Default) {
                stations.getAll()
            }.await()
        }
    }*/

    Column {
        SearchBars()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBars() {
    var departureText by remember { mutableStateOf("") }
    var destinationText by remember { mutableStateOf("") }
    var departureActive by remember { mutableStateOf(false) }
    var destinationActive by remember { mutableStateOf(false) }

    Box(Modifier
        .semantics { isTraversalGroup = true }
        .zIndex(1f)
        .fillMaxWidth()
    ) {
        // Departure Search Box
        DockedSearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
            query = departureText,
            onQueryChange = { query ->
                departureText = query
            },
            onSearch = {
                departureActive = false
                destinationActive = false
            },
            active = departureActive,
            onActiveChange = {
                departureActive = it
                destinationActive = !it
            },
            placeholder = { Text(text = stringResource(id = R.string.trains_departure_search_hint)) },
            leadingIcon = { Icon(Icons.Rounded.SouthEast, null) }
        ) {

        }
    }
    Box(Modifier
        .semantics { isTraversalGroup = true }
        .zIndex(1f)
        .fillMaxWidth()
    ) {
        // Destination Search Box
        DockedSearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
            query = destinationText,
            onQueryChange = { query ->
                destinationText = query
            },
            onSearch = {
                destinationActive = false
                departureActive = false
            },
            active = destinationActive,
            onActiveChange = {
                destinationActive = it
                departureActive = !it
            },
            placeholder = { Text(text = stringResource(id = R.string.trains_destination_search_hint)) },
            leadingIcon = { Icon(Icons.Rounded.NorthEast, null) }
        ) {

        }
    }
}