package dev.alphexo.movmentor.train

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ScreensTrain(
    val title: String,
    val route: String,
    val icon: (isSelected: Boolean) -> ImageVector
) {
    data object Search : ScreensTrain(
        title = "Search",
        route = "search",
        icon = { isSelected ->
            if (isSelected) {
                Icons.Rounded.Search
            } else {
                Icons.Outlined.Search
            }
        }
    )

    data object Timetable : ScreensTrain(
        title = "Timetable",
        route = "timetable",
        icon = { isSelected ->
            if (isSelected) {
                Icons.Rounded.Timer
            } else {
                Icons.Outlined.Timer
            }
        }
    )

    data object History : ScreensTrain(
        title = "History",
        route = "history",
        icon = { isSelected ->
            if (isSelected) {
                Icons.Rounded.History
            } else {
                Icons.Outlined.History
            }
        }
    )
}
