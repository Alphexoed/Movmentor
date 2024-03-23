package dev.alphexo.movmentor.train.tabs.timetable


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.alphexo.movmentor.R
import dev.alphexo.movmentor.train.endpoints.FromToDate
import dev.alphexo.movmentor.train.endpoints.FromToDateKey
import dev.alphexo.movmentor.train.endpoints.Timetable
import dev.alphexo.movmentor.train.models.TimetableResultModel
import dev.alphexo.movmentor.train.models.data.SearchQuery
import dev.alphexo.movmentor.train.models.data.TimetableResult
import dev.alphexo.movmentor.train.models.data.buildTimetableResult
import dev.alphexo.movmentor.train.models.data.getServiceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CurrentTime {
    private val calendar = Calendar.getInstance().apply { isLenient = false }

    val date: String get() = format("yyyy-MM-dd")
    val time: String get() = format("HH:mm")
    val get: Date get() = calendar.time

    var hours: Int
        get() = calendar.get(Calendar.HOUR_OF_DAY)
        set(value) {
            calendar.set(Calendar.HOUR_OF_DAY, value)
        }

    var minutes: Int
        get() = calendar.get(Calendar.MINUTE)
        set(value) {
            calendar.set(Calendar.MINUTE, value)
        }

    fun format(pattern: String): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(calendar.time)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableTab() {
    val currentTime by remember { mutableStateOf(CurrentTime()) }
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val timetableEndpoint = Timetable()
    val searchQueryList = remember { mutableStateListOf<SearchQuery>() }
    val timetableResultList = remember { mutableStateListOf<TimetableResult>() }
    var stationsEndpointResult by remember { mutableStateOf<JSONArray?>(null) }
    var stationsTripsResult by remember { mutableStateOf<JSONObject?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var showTimePicker by remember { mutableStateOf(false) }
    val stateTimePicker = rememberTimePickerState(
        initialMinute = currentTime.minutes,
        initialHour = currentTime.hours,
        is24Hour = true
    )

    if (showTimePicker) {
        BasicAlertDialog(
            onDismissRequest = { showTimePicker = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(size = 12.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = Color.LightGray.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(size = 12.dp)
                    )
                    .padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // time picker
                TimePicker(state = stateTimePicker)

                // buttons
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // dismiss button
                    TextButton(onClick = { showTimePicker = false }) {
                        Text(text = "Dismiss")
                    }

                    // confirm button
                    TextButton(
                        onClick = {
                            currentTime.hours = stateTimePicker.hour
                            currentTime.minutes = stateTimePicker.minute

                            Log.w(
                                "FromToDate.Entered",
                                "Entered time: ${currentTime.format(currentTime.time)}"
                            )

                            showTimePicker = false
                        }
                    ) {
                        Text(text = "Confirm")
                    }
                }
            }
        }
    }


    Box(
        Modifier
            .fillMaxSize()
    ) {
        // Talkback focus order sorts based on x and y position before considering z-index. The
        // extra Box with semantics and fillMaxWidth is a workaround to get the search bar to focus
        // before the content.
        Box(Modifier
            .semantics { isTraversalGroup = true }
            .zIndex(1f)
            .fillMaxWidth()
//            .background(Color.Green)
        ) {
            DockedSearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
                query = text,
                onQueryChange = { query ->
                    text = query

                    coroutineScope.launch {
                        // Perform the network call on the background thread
                        coroutineScope.async(Dispatchers.Default) {
                            timetableEndpoint.stationName(text) { result: JSONArray ->
                                stationsEndpointResult = result
                            }
                        }.await()

                        // Handle the result on the main thread
                        stationsEndpointResult?.let { searchQueryLogic(searchQueryList, it) }
                    }
                },
                onSearch = {
                    active = false
                },
                active = active,
                onActiveChange = { active = it },
                placeholder = { Text(text = stringResource(id = R.string.trains_search_hint)) },
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                trailingIcon = {
                    IconButton(onClick = {
                        showTimePicker = true
                    }) {
                        Icon(imageVector = Icons.Rounded.Schedule, null)
                    }
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Create a copy to avoid ConcurrentModificationException
                    val searchQueries = searchQueryList.toList()

                    searchQueries.forEachIndexed { index, searchQuery ->
                        item(key = index) {
                            ListItem(
                                modifier = Modifier
                                    .clickable {
                                        text = searchQuery.name
                                        active = false
                                        isLoading = true
                                        timetableResultList.clear()

                                        coroutineScope.launch {
                                            val f2d = FromToDate()

                                            f2d.from = mapOf(
                                                FromToDateKey.DATE to currentTime.date,
                                                FromToDateKey.HOUR to currentTime.time
                                            )

                                            f2d.to = mapOf(
                                                FromToDateKey.DATE to currentTime.date,
                                                FromToDateKey.HOUR to "23:59"
                                            )


                                            // Perform the network call on the background thread
                                            coroutineScope.async(Dispatchers.Default) {
                                                timetableEndpoint.getTimetable(
                                                    searchQuery.nodeId,
                                                    f2d
                                                ) { result ->
                                                    stationsTripsResult = result
                                                }
                                            }.await()

                                            // Handle the result on the main thread
                                            isLoading = false
                                            stationsTripsResult?.let {
                                                searchTripResultLogic(timetableResultList, it)
                                            }
                                        }
                                    },
                                headlineContent = {
                                    Text(text = searchQuery.name)
                                },
                                supportingContent = {
                                    Text(text = searchQuery.nodeId.toString())
                                }
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(visible = isLoading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
                Text(text = stringResource(R.string.please_wait), Modifier.padding(top = 12.dp))
            }
        }


        // Want to display value by default ? Let's do it here
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            timetableResultList.forEachIndexed { index, trip ->
                item(key = index) {
                    val f2d = FromToDate()

                    f2d.from = mapOf(
                        FromToDateKey.DATE to trip.tripDate,
                        FromToDateKey.HOUR to trip.departureDate
                    )

                    f2d.to = mapOf(
                        FromToDateKey.DATE to trip.tripDate,
                        FromToDateKey.HOUR to trip.departureDate
                    )

                    TimetableResultModel(
                        service = getServiceType(trip.service),
                        trainNumber = trip.trainNumber1,
                        platform = trip.platform,
                        operator = trip.operator,
                        trainPassed = trip.trainPassed,
                        departureStation = trip.departureStationName,
                        destinationStation = trip.destinationStationName,
                        fromToDate = f2d,
                        warnings = trip.warnings.ifEmpty { null }
                    )
                }
            }
        }
    }
}

fun searchQueryLogic(searchQueryList: MutableList<SearchQuery>, stationsEndpointResult: JSONArray) {
    searchQueryList.clear()

    stationsEndpointResult.let { stations ->
        stations.let {
            for (index in 0 until stations.length()) {
                val station = stations.optJSONObject(index)
                val distancia = station.optInt("Distancia", 0)
                val nodeId = station.optInt("NodeID", 0)
                val nome = station.optString("Nome", "undefined")
                searchQueryList.add(SearchQuery(distancia, nodeId, nome))
            }
        }
    }
}


fun searchTripResultLogic(
    timetableResultList: MutableList<TimetableResult>,
    stationsTripsResult: JSONObject
) {
    timetableResultList.clear()

    val tripResultInfra: List<JSONObject> = mutableListOf<JSONObject>().apply {
        val jsonArray = stationsTripsResult.optJSONArray("resp:infra")
        for (index in 0 until (jsonArray?.length() ?: 0)) {
            add(jsonArray!!.getJSONObject(index))
        }
    }

    val tripResultCP: List<JSONObject> = mutableListOf<JSONObject>().apply {
        val jsonArray = stationsTripsResult.getJSONArray("resp:cp")
        for (index in 0 until jsonArray.length()) {
            add(jsonArray.getJSONObject(index))
        }
    }

    val cpData = tripResultCP.map { it: JSONObject ->
        mapOf(
            "trainNumber" to it.getInt("trainNumber").toString(),
            "platform" to it.optString("platform")
        )
    }

    tripResultInfra.forEach { infraObject ->
        val cpPlatform =
            cpData.find { it["trainNumber"] == infraObject.getString("NComboio1") }?.get("platform")
        infraObject.put("CP:Plataforma", cpPlatform)
        timetableResultList.add(buildTimetableResult(infraObject))
    }
}
