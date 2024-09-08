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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import dev.alphexo.movmentor.R
import dev.alphexo.movmentor.train.endpoints.FromToDate
import dev.alphexo.movmentor.train.endpoints.FromToDateKey
import dev.alphexo.movmentor.train.endpoints.Stations
import dev.alphexo.movmentor.train.endpoints.Timetable
import dev.alphexo.movmentor.train.models.TimetableResultModel
import dev.alphexo.movmentor.train.models.data.SearchQuery
import dev.alphexo.movmentor.train.models.data.TimetableResult
import dev.alphexo.movmentor.train.models.data.getServiceType
import dev.alphexo.movmentor.utils.MiscTrains
import dev.alphexo.movmentor.utils.toJSONObjectList
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

    fun addPlusDays(days: Int) {
        calendar.add(Calendar.DAY_OF_MONTH, days)
    }

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
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun TimetableTab() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val currentTime by remember { mutableStateOf(CurrentTime()) }
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val miscTrains = remember { MiscTrains() }
    val timetableEndpoint = Timetable()
    val stationsEndpoint = Stations()
    val searchQueryList = remember { mutableStateListOf<SearchQuery>() }
    val timetableResultList = remember { mutableStateListOf<TimetableResult>() }
    var stationsEndpointResult by remember { mutableStateOf<JSONArray?>(null) }
    val stationsTripsResult = remember { mutableStateOf<JSONObject?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var showTimePicker by remember { mutableStateOf(false) }
    val stateTimePicker = rememberTimePickerState(
        initialMinute = currentTime.minutes, initialHour = currentTime.hours, is24Hour = true
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
                    TextButton(onClick = {
                        currentTime.hours = stateTimePicker.hour
                        currentTime.minutes = stateTimePicker.minute

                        Log.w(
                            "FromToDate.Entered",
                            "Entered time: ${currentTime.format(currentTime.time)}"
                        )

                        showTimePicker = false
                    }) {
                        Text(text = "Confirm")
                    }
                }
            }
        }
    }

    Box(
        Modifier.fillMaxSize()
    ) {
        // Talkback focus order sorts based on x and y position before considering z-index. The
        // extra Box with semantics and fillMaxWidth is a workaround to get the search bar to focus
        // before the content.
        Box(
            Modifier
                .fillMaxSize()
                .semantics { isTraversalGroup = true }) {
            DockedSearchBar(modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
                .semantics { traversalIndex = 0f }, inputField = {
                SearchBarDefaults.InputField(query = text,
                    onSearch = {
                        active = false
                    },
                    onQueryChange = { it ->
                        text = it
                        coroutineScope.launch {
                            // Perform the network call on the background thread
                            coroutineScope.async(Dispatchers.Default) {
                                stationsEndpoint.fromName(text) { result ->
                                    stationsEndpointResult = result as JSONArray
                                }
                            }.await()

                            // Handle the result on the main thread
                            stationsEndpointResult?.let {
                                miscTrains.searchQueryLogic(
                                    searchQueryList, it
                                )
                            }
                        }
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text(stringResource(id = R.string.trains_search_hint)) },
                    leadingIcon = { Icon(Icons.Rounded.Search, null) },
                    trailingIcon = {
                        IconButton(onClick = {
                            showTimePicker = true
                        }) {
                            Icon(imageVector = Icons.Rounded.Schedule, null)
                        }
                    })
            }, expanded = expanded, onExpandedChange = { expanded = it }) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Create a copy to avoid ConcurrentModificationException
                    val searchQueries = searchQueryList.toList()

                    searchQueries.forEachIndexed { index, searchQuery ->
                        item(key = index) {
                            ListItem(modifier = Modifier
                                .clickable {
                                    text = searchQuery.name
                                    active = false
                                    isLoading = true
                                    expanded = false
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
                                        coroutineScope
                                            .async(Dispatchers.Default) {
                                                timetableEndpoint.getTimetable(
                                                    searchQuery.nodeId, f2d
                                                ) { result ->
                                                    stationsTripsResult.value = result
                                                }
                                            }
                                            .await()

                                        // Handle the result on the main thread
                                        isLoading = false

                                        stationsTripsResult.value.let { jsonObject ->
                                            Log.i(
                                                "stationsTripsResult.i", jsonObject.toString()
                                            )
                                            if (jsonObject != null) {
                                                miscTrains.searchTripResultLogic(
                                                    timetableResultList, jsonObject
                                                )
                                            }
                                        }
                                    }
                                }
                                .fillMaxWidth(),
                                headlineContent = {
                                    Text(searchQuery.name)
                                },
                                supportingContent = {
                                    Text(searchQuery.nodeId.toString())
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp
                ), verticalArrangement = Arrangement.spacedBy(8.dp)
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

                        TimetableResultModel(service = getServiceType(trip.service),
                            trainNumber = trip.trainNumber1,
                            platform = trip.platform,
                            operator = trip.operator,
                            trainPassed = trip.trainPassed,
                            currentStation = text,
                            departureStation = trip.departureStationName,
                            destinationStation = trip.destinationStationName,
                            fromToDate = f2d,
                            warnings = trip.warnings.ifEmpty { null })
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
    }
}

