package dev.alphexo.movmentor.train.tabs.timetable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.alphexo.movmentor.R
import dev.alphexo.movmentor.train.endpoints.EnumStopCard
import dev.alphexo.movmentor.train.endpoints.Trip
import dev.alphexo.movmentor.train.models.data.TripSchedule
import dev.alphexo.movmentor.train.models.data.TripStop
import dev.alphexo.movmentor.train.models.data.buildTripSchedule
import dev.alphexo.movmentor.ui.theme.MovmentorTheme
import dev.alphexo.movmentor.ui.theme.informationContainer
import dev.alphexo.movmentor.ui.theme.onInformationContainer
import dev.alphexo.movmentor.ui.theme.onSuccessContainer
import dev.alphexo.movmentor.ui.theme.onWarningContainer
import dev.alphexo.movmentor.ui.theme.successContainer
import dev.alphexo.movmentor.ui.theme.warningContainer
import dev.alphexo.movmentor.utils.DateFormats
import dev.alphexo.movmentor.utils.convertDateFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class TrainScheduleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentStation: String = intent.getStringExtra("currentStation") ?: "null"
        val trainNumber: Int = intent.getIntExtra("trainNumber", -1)
        val date: String = convertDateFormat(
            originalFormat = DateFormats.dayMonthYear,
            desiredFormat = DateFormats.yearMonthDay,
            dateString = intent.getStringExtra("date") ?: "null"
        )

        enableEdgeToEdge()
        setContent {
            MovmentorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        ScheduleContent(trainNumber, date, currentStation)
                    }
                }
            }
        }
    }
}

@Composable
fun titleCardData(index: Int, value: TripSchedule?): List<Any> {
    val (title: String, data: Pair<String?, Modifier>) = when (index) {
        0 -> "Destination" to (value?.destinationStationName to Modifier.padding(
            bottom = 4.dp,
            end = 4.dp
        ))

        1 -> "Duration" to (value?.tripDuration to Modifier.padding(start = 4.dp, bottom = 4.dp))
        2 -> "Departure" to (value?.departureDate to Modifier.padding(top = 4.dp, end = 4.dp))
        3 -> "Destination" to (value?.destinationDate to Modifier.padding(start = 4.dp, top = 4.dp))
        else -> throw IllegalArgumentException("Invalid index: $index (expected 0..3)") // Specific exception
    }

    return listOf(title, data.first ?: stringResource(R.string.please_wait), data.second)
}

@Composable
fun ScheduleContent(trainNumber: Int, date: String, currentStation: String) {
    val tripEndpoint = Trip()
    var isLoading by remember { mutableStateOf(true) }
    val tripSchedule = remember { mutableStateOf<TripSchedule?>(null) }

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

    LaunchedEffect("schedule-fetch") {
        launch {
            // Perform the network call on the background thread
            async(Dispatchers.Default) {
                tripEndpoint.fromTrainNumber(
                    trainNumber, date
                ) { result ->
                    tripSchedule.value = buildTripSchedule(result)
                }
            }.await()

            isLoading = false
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Title Stuff
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), modifier = Modifier.fillMaxWidth()
        ) {
            items(4) { index ->
                titleCardData(index, tripSchedule.value).let {
                    TitleItemCard(
                        title = it[0] as String,
                        body = it[1] as String,
                        modifier = it[2] as Modifier
                    )
                }
            }
        }

        // Body Stuff
        LazyColumn(
            modifier = Modifier.padding(top = 8.dp)
        ) {
            tripSchedule.value?.schedule?.forEachIndexed { index, tripStop ->
                item(index) {
                    StopItem(tripStop, currentStation)
                }
            }
        }
    }
}

@Composable
fun TitleItemCard(title: String, body: String, modifier: Modifier) {
    Card(modifier) {
        // Content of the card
        Column(Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Text(text = body)
        }
    }
}

@Composable
fun StopItem(tripStop: TripStop, currentStation: String) {
    Column(
        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row {
                val iconType = when {
                    tripStop.stationName == currentStation -> EnumStopCard.CURRENT
                    tripStop.trainPassed -> EnumStopCard.PASSED
                    else -> EnumStopCard.PENDING
                }
                StopCardIcon(type = iconType)

                if (tripStop.warnings.isNotEmpty() || tripStop.warnings === "null") {
                    Spacer(modifier = Modifier.size(4.dp))
                    StopCardIcon(type = EnumStopCard.WARNING)
                }
            }

            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = tripStop.stationName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = tripStop.scheduledHour,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
            )
        }
    }
}


@Composable
fun StopCardIcon(type: EnumStopCard) {
    val (icon: ImageVector, colors: Pair<Color, Color>) = when (type) {
        EnumStopCard.CURRENT -> Icons.Rounded.StarOutline to (MaterialTheme.colorScheme.warningContainer to MaterialTheme.colorScheme.onWarningContainer)
        EnumStopCard.PENDING -> Icons.Rounded.HourglassEmpty to (MaterialTheme.colorScheme.informationContainer to MaterialTheme.colorScheme.onInformationContainer)
        EnumStopCard.PASSED -> Icons.Rounded.CheckCircleOutline to (MaterialTheme.colorScheme.successContainer to MaterialTheme.colorScheme.onSuccessContainer)
        EnumStopCard.WARNING -> Icons.Rounded.WarningAmber to (MaterialTheme.colorScheme.warningContainer to MaterialTheme.colorScheme.onWarningContainer)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = colors.first
        )
    ) {
        Icon(
            imageVector = icon,
            tint = colors.second,
            contentDescription = null,
            modifier = Modifier.padding(8.dp)
        )
    }
}
