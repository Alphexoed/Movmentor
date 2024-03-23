package dev.alphexo.movmentor.train

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.alphexo.movmentor.train.tabs.history.HistoryTab
import dev.alphexo.movmentor.train.tabs.search.SearchTab
import dev.alphexo.movmentor.train.tabs.timetable.TimetableTab


@Composable
fun BottomDrawerNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = ScreensTrain.Search.route,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.BottomCenter
    ) {
        composable(ScreensTrain.Search.route) {
            SearchTab()
        }
        composable(ScreensTrain.Timetable.route) {
            TimetableTab()
        }
        composable(ScreensTrain.History.route) {
            HistoryTab()
        }
    }
}