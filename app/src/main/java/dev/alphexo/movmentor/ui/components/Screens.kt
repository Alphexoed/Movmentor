package dev.alphexo.movmentor.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Subway
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Subway
import androidx.compose.material.icons.rounded.Train
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(
    val title: String,
    val route: String,
    val icon: (isSelected: Boolean) -> ImageVector,
    val bottom: Boolean = false
) {
    data object Home : Screens(
        title = "Home",
        route = "home",
        icon = { isSelected ->
            if (isSelected) {
                Icons.Rounded.Home
            } else {
                Icons.Outlined.Home
            }
        }
    )

    data object Train : Screens(
        title = "Train",
        route = "train",
        icon = { isSelected ->
            if (isSelected) {
                Icons.Rounded.Train
            } else {
                Icons.Outlined.Train
            }
        }
    )

    data object Metro : Screens(
        title = "Metro",
        route = "metro",
        icon = { isSelected ->
            if (isSelected) {
                Icons.Rounded.Subway
            } else {
                Icons.Outlined.Subway
            }
        }
    )

    data object Bus : Screens(
        title = "Bus",
        route = "bus",
        icon = { isSelected ->
            if (isSelected) {
                Icons.Rounded.DirectionsBus
            } else {
                Icons.Outlined.DirectionsBus
            }
        }
    )

    data object Settings : Screens(
        title = "Settings",
        route = "settings",
        icon = { isSelected ->
            if (isSelected) {
                Icons.Rounded.Settings
            } else {
                Icons.Outlined.Settings
            }
        },
        bottom = true
    )
}