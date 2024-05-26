package dev.alphexo.movmentor.train.tabs.timetable

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class TrainScheduleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentStation: String = intent.getStringExtra("currentStation") ?: "null"
        val trainNumber: Int = intent.getIntExtra("trainNumber", -1)
        val warnings: String? = intent.getStringExtra("warnings")
        val date: String = convertDateFormat(
            originalFormat = DateFormats.dayMonthYear,
            desiredFormat = DateFormats.yearMonthDay,
            dateString = intent.getStringExtra("date") ?: "null"
        )

        enableEdgeToEdge()
        setContent {
            MovmentorTheme {
                val coroutineScope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }, modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        ScheduleContent(
                            trainNumber,
                            warnings,
                            date,
                            currentStation,
                            coroutineScope,
                            snackbarHostState
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ScheduleContent(
    trainNumber: Int,
    warnings: String?,
    date: String,
    currentStation: String,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
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
        Row {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
            ) {
                // Content of the card
                Column(Modifier.padding(16.dp)) {
                    Text(text = "Destination", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = tripSchedule.value?.destinationStationName
                            ?: stringResource(R.string.please_wait)
                    )
                }
            }
            Card(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(start = 4.dp)
            ) {
                // Content of the card
                Column(Modifier.padding(16.dp)) {
                    Text(text = "Duration", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = tripSchedule.value?.tripDuration
                            ?: stringResource(R.string.please_wait)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = !warnings.isNullOrEmpty(),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.warningContainer
                )
            ) {
                Row(Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.WarningAmber,
                        tint = MaterialTheme.colorScheme.onWarningContainer,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp)
                    )

                    Text(text = warnings!!)
                }
            }
        }

        // Body Stuff
        LazyColumn(
            modifier = Modifier.padding(top = 8.dp)
        ) {
            tripSchedule.value?.schedule?.forEachIndexed { index, tripStop ->
                item(index) {
                    StopItem(tripStop, currentStation, coroutineScope, snackbarHostState)
                }
            }
        }
    }
}

@Composable
fun StopItem(
    tripStop: TripStop,
    currentStation: String,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
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
                StopCardIcon(iconType)

                if (tripStop.warnings.isNotEmpty() || tripStop.warnings === "null") {
                    Spacer(modifier = Modifier.size(4.dp))
                    Box(Modifier.clickable {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = tripStop.warnings,
                                withDismissAction = true,
                                duration = SnackbarDuration.Long
                            )
                        }
                    }) {
                        StopCardIcon(EnumStopCard.WARNING)
                    }
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
fun StopCardIcon(
    type: EnumStopCard
) {
    val (icon: ImageVector, colors: Pair<Color, Color>) = when (type) {
        EnumStopCard.CURRENT -> Icons.Rounded.LocationOn to (MaterialTheme.colorScheme.warningContainer to MaterialTheme.colorScheme.onWarningContainer)
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
