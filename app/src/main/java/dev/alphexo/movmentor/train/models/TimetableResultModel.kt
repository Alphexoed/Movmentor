package dev.alphexo.movmentor.train.models

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.MiscellaneousServices
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.alphexo.movmentor.train.endpoints.FromToDate
import dev.alphexo.movmentor.train.endpoints.FromToDateKey
import dev.alphexo.movmentor.train.models.data.ServiceType
import dev.alphexo.movmentor.train.tabs.timetable.TrainScheduleActivity
import dev.alphexo.movmentor.ui.theme.Typography
import dev.alphexo.movmentor.ui.theme.onWarningContainer
import dev.alphexo.movmentor.ui.theme.warningContainer


@Composable
fun TimetableResultModel(
    service: ServiceType = ServiceType.UNKNOWN,
    trainNumber: Int = -1,
    platform: String? = "undefined",
    operator: String = "undefined",
    trainPassed: Boolean = false,
    currentStation: String? = "undefined",
    departureStation: String = "undefined",
    destinationStation: String = "undefined",
    fromToDate: FromToDate? = null,
    warnings: String? = null
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (trainPassed) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                Intent(context, TrainScheduleActivity::class.java).let {
                    it.putExtra("currentStation", currentStation)
                    it.putExtra("trainNumber", trainNumber)
                    it.putExtra("warnings", warnings)
                    it.putExtra("date", fromToDate?.from?.get(FromToDateKey.DATE))
                    context.startActivity(it)
                }
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            TimetableResultLocations(departureStation, destinationStation, fromToDate)
            TimetableResultData(service, trainNumber, platform, operator, warnings)
        }
    }
}

@Composable
fun TimetableResultLocations(
    departureStation: String,
    destinationStation: String,
    fromToDate: FromToDate?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = departureStation,
            style = Typography.titleMedium
        )
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = "null",
            modifier = Modifier.padding(start = 4.dp, end = 4.dp)
        )
        Text(
            text = destinationStation,
            style = Typography.titleMedium
        )

    }
    Text(
        text = fromToDate?.from?.get(FromToDateKey.HOUR) ?: "undefined"
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimetableResultData(
    service: ServiceType,
    trainNumber: Int,
    platform: String?,
    operator: String,
    warnings: String?
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(Modifier.padding(top = 8.dp)) {
            ServiceIconModel(service)
        }
        SearchResultTrainNumber(trainNumber)
        if (!platform.isNullOrEmpty()) {
            SearchResultPlatform(platform)
        }
        SearchResultOperator(operator)
        SearchResultWarnings(warnings)
    }
}


@Composable
fun SearchResultTrainNumber(trainNumber: Int) {
    SuggestionChip(
        onClick = {},
        label = {
            Text(text = trainNumber.toString())
        },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Numbers,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    )
}

@Composable
fun SearchResultOperator(operator: String) {
    SuggestionChip(
        onClick = {},
        label = {
            Text(text = operator)
        },
        icon = {
            Icon(
                imageVector = Icons.Rounded.MiscellaneousServices,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    )
}

@Composable
fun SearchResultPlatform(platform: String) {
    SuggestionChip(
        onClick = {},
        label = {
            Text(text = platform)
        },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Layers,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    )
}

@Composable
fun SearchResultWarnings(warnings: String?) {
    if (warnings != null) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.warningContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.WarningAmber,
                    tint = MaterialTheme.colorScheme.onWarningContainer,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = warnings,
                    color = MaterialTheme.colorScheme.onWarningContainer
                )
            }

        }
    }
}