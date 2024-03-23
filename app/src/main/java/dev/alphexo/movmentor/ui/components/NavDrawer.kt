package dev.alphexo.movmentor.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.alphexo.movmentor.R
import dev.alphexo.movmentor.ui.theme.Typography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun NavDrawer() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: Screens.Home
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val screens = listOf(
        Screens.Home,
        Screens.Train,
        Screens.Metro,
        Screens.Bus,
        Screens.Settings,
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                HorizontalDivider(modifier = Modifier.padding(bottom = 20.dp))

                //? Here are the TOP navigation items
                screens.filter { !it.bottom }.forEach { screen ->
                    DrawerItem(screen, currentRoute, navController, coroutineScope, drawerState)
                }

                //? Here are the BOTTOM navigation items
                if (screens.any { it.bottom }) {
                    Spacer(modifier = Modifier.weight(1F))
                }
                screens.filter { it.bottom }.forEach { bottomScreen ->
                    DrawerItem(
                        bottomScreen,
                        currentRoute,
                        navController,
                        coroutineScope,
                        drawerState
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
            }
        }
    ) {
        Surface {
            DrawerNavigation(navController)
        }
    }
}

@Composable
fun DrawerHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = Typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}


@Composable
fun DrawerItem(
    screen: Screens,
    currentRoute: Any,
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState
) {
    NavigationDrawerItem(
        selected = currentRoute == screen.route,
        label = { Text(text = screen.title) },
        icon = {
            Icon(
                imageVector = screen.icon(currentRoute == screen.route),
                contentDescription = "${screen.title} icon"
            )
        },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        onClick = {
            navController.navigate(screen.route) {
                launchSingleTop = true
                //restoreState = true
            }
            coroutineScope.launch {
                drawerState.close()
            }
        }
    )
}