package dev.alphexo.movmentor.train.tabs.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.NorthEast
import androidx.compose.material.icons.rounded.SouthEast
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.alphexo.movmentor.R
import dev.alphexo.movmentor.train.endpoints.FromToDate
import dev.alphexo.movmentor.train.endpoints.FromToDateKey
import dev.alphexo.movmentor.train.endpoints.Stations
import dev.alphexo.movmentor.train.endpoints.Trip
import dev.alphexo.movmentor.train.models.data.SearchQuery
import dev.alphexo.movmentor.train.models.data.TripCalculate
import dev.alphexo.movmentor.train.tabs.timetable.CurrentTime
import dev.alphexo.movmentor.utils.MiscTrains
import dev.alphexo.movmentor.utils.calculateNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray


@Composable
@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showSystemUi = true, showBackground = true)
fun SearchTab() {
    val currentTime by remember { mutableStateOf(CurrentTime()) }
    var departureText by remember { mutableStateOf("") }
    var destinationText by remember { mutableStateOf("") }
    var departureActive by remember { mutableStateOf(false) }
    var destinationActive by remember { mutableStateOf(false) }
    var departureQuery by remember { mutableStateOf<SearchQuery?>(null) }
    val destinationQuery by remember { mutableStateOf<SearchQuery?>(null) }
    val departureSearchQueryList = remember { mutableStateListOf<SearchQuery>() }
    val destinationSearchQueryList = remember { mutableStateListOf<SearchQuery>() }
    val tripsResultList = remember { mutableStateListOf<TripCalculate>() }
    val coroutineScope = rememberCoroutineScope()
    val miscTrains = remember { MiscTrains() }
    val stationsEndpoint = Stations()
    var stationsEndpointResult by remember { mutableStateOf<JSONArray?>(null) }

    fun handleListItemClick(searchQuery: SearchQuery) {
        departureQuery = searchQuery
        departureText = searchQuery.name
        departureActive = false

        coroutineScope.launch {
            executeTrip(
                departureQuery, destinationQuery, tripsResultList, coroutineScope, currentTime
            )
        }
    }


    /*
    Column {
        Box(
            Modifier
                .semantics { isTraversalGroup = true }
                .zIndex(1f)
                .fillMaxWidth()) {
            DockedSearchBar(modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
                query = departureText,
                onQueryChange = { query ->
                    departureText = query

                    coroutineScope.launch {
                        // Perform the network call on the background thread
                        coroutineScope.async(Dispatchers.Default) {
                            stationsEndpoint.fromName(departureText) { result ->
                                stationsEndpointResult = result as JSONArray
                            }
                        }.await()

                        // Handle the result on the main thread
                        stationsEndpointResult?.let {
                            miscTrains.searchQueryLogic(
                                departureSearchQueryList, it
                            )
                        }
                    }
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
                leadingIcon = { Icon(Icons.Rounded.SouthEast, null) }) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Create a copy to avoid ConcurrentModificationException
                    val searchQueries = departureSearchQueryList.toList()

                    searchQueries.forEachIndexed { index, searchQuery ->
                        item(key = index) {
                            ListItem(modifier = Modifier.clickable {
                                handleListItemClick(searchQuery)
                            }, headlineContent = {
                                Text(text = searchQuery.name)
                            }, supportingContent = {
                                Text(text = searchQuery.nodeId.toString())
                            })
                        }
                    }
                }
            }
        }
        Box(
            Modifier
                .semantics { isTraversalGroup = true }
                .zIndex(1f)
                .fillMaxWidth()) {

            DockedSearchBar(modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
                query = destinationText,
                onQueryChange = { query ->
                    destinationText = query

                    coroutineScope.launch {
                        // Perform the network call on the background thread
                        coroutineScope.async(Dispatchers.Default) {
                            stationsEndpoint.fromName(destinationText) { result ->
                                stationsEndpointResult = result as JSONArray
                            }
                        }.await()

                        // Handle the result on the main thread
                        stationsEndpointResult?.let {
                            miscTrains.searchQueryLogic(
                                destinationSearchQueryList, it
                            )
                        }
                    }
                },
                onSearch = {
                    departureActive = false
                    destinationActive = false
                },
                active = destinationActive,
                onActiveChange = {
                    departureActive = !it
                    destinationActive = it
                },
                placeholder = { Text(text = stringResource(id = R.string.trains_destination_search_hint)) },
                leadingIcon = { Icon(Icons.Rounded.NorthEast, null) }) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Create a copy to avoid ConcurrentModificationException
                    val searchQueries = destinationSearchQueryList.toList()

                    searchQueries.forEachIndexed { index, searchQuery ->
                        item(key = index) {
                            ListItem(modifier = Modifier.clickable {
                                handleListItemClick(searchQuery)
                            }, headlineContent = {
                                Text(text = searchQuery.name)
                            }, supportingContent = {
                                Text(text = searchQuery.nodeId.toString())
                            })
                        }
                    }
                }
            }
        }
    }


     */
}






private suspend fun executeTrip(
    dep: SearchQuery?,
    dest: SearchQuery?,
    trips: MutableList<TripCalculate>,
    scope: CoroutineScope,
    now: CurrentTime
) {
    Log.w("dep?.name", dep?.name.toString())
    Log.w("dest?.name", dest?.name.toString())
    Log.w("calc?", (dep?.name.isNullOrEmpty() || dest?.name.isNullOrEmpty()).toString())

    if (dep?.name.isNullOrEmpty() || dest?.name.isNullOrEmpty()) return

    Log.w("executeTrigger", "OK!!!!")
    trips.clear()

    val f2d = FromToDate().apply {
        single = mapOf(FromToDateKey.DATE to now.date, FromToDateKey.HOUR to now.time)
    }

    // Perform the network call on the background thread
    with(scope) {
        async(Dispatchers.Default) {
            Trip().calculateTrip(
                Pair(
                    calculateNode(dep!!.nodeId.toString()), calculateNode(dest!!.nodeId.toString())
                ), f2d
            ) { _: Int, response: TripCalculate ->
                Log.w("TripCalculate", "Received ${response.trips.size} trips")
                Log.w("TripCalculate", "${response.trips}")
            }
        }.await()


    }

    // Handle the response on the main thread (use response data here)

}


