package dev.alphexo.movmentor.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.alphexo.movmentor.HomeScreen
import dev.alphexo.movmentor.bus.BusScreen
import dev.alphexo.movmentor.metro.MetroScreen
import dev.alphexo.movmentor.settings.SettingsScreen
import dev.alphexo.movmentor.train.TrainScreen

@Composable
fun DrawerNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.Home.route
    ) {
        composable(Screens.Home.route) {
            HomeScreen()
        }
        composable(Screens.Train.route) {
            TrainScreen()
        }
        composable(Screens.Metro.route) {
            MetroScreen()
        }
        composable(Screens.Bus.route) {
            BusScreen()
        }
        composable(Screens.Settings.route) {
            SettingsScreen()
        }
    }
}