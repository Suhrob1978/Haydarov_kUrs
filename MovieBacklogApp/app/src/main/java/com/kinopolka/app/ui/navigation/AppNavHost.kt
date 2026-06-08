package com.kinopolka.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kinopolka.app.ui.auth.LoginScreen
import com.kinopolka.app.ui.auth.RegisterScreen
import com.kinopolka.app.ui.backlog.BacklogScreen
import com.kinopolka.app.ui.catalog.CatalogScreen
import com.kinopolka.app.ui.profile.ProfileScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
}

@Composable
fun AppNavHost(startLoggedIn: Boolean) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = if (startLoggedIn) Routes.MAIN else Routes.LOGIN,
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoggedIn = { navController.navigateClearing(Routes.MAIN) },
                onNavigateRegister = { navController.navigate(Routes.REGISTER) },
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegistered = { navController.navigateClearing(Routes.MAIN) },
                onNavigateLogin = { navController.popBackStack() },
            )
        }
        composable(Routes.MAIN) {
            MainScreen(onLoggedOut = { navController.navigateClearing(Routes.LOGIN) })
        }
    }
}

private fun NavHostController.navigateClearing(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { inclusive = true }
        launchSingleTop = true
    }
}

private enum class Tab(val route: String, val label: String, val icon: ImageVector) {
    Catalog("tab_catalog", "Каталог", Icons.Filled.Movie),
    Backlog("tab_backlog", "Бэклог", Icons.Filled.List),
    Profile("tab_profile", "Профиль", Icons.Filled.Person),
}

@Composable
private fun MainScreen(onLoggedOut: () -> Unit) {
    val tabNav = rememberNavController()
    val backStackEntry by tabNav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            tabNav.navigate(tab.route) {
                                popUpTo(tabNav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = tabNav,
            startDestination = Tab.Catalog.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Tab.Catalog.route) { CatalogScreen() }
            composable(Tab.Backlog.route) { BacklogScreen() }
            composable(Tab.Profile.route) { ProfileScreen(onLoggedOut = onLoggedOut) }
        }
    }
}
