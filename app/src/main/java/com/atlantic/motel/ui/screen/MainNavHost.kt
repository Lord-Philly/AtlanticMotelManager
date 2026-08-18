package com.atlantic.motel.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.atlantic.motel.AtlanticMotelApp

@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    val hasSession = AtlanticMotelApp.instance.currentUser != null

    NavHost(
        navController = navController,
        startDestination = if (hasSession) "main" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("main") {
            MainScreen(
                onStayClick = { apartmentId ->
                    navController.navigate("stay/$apartmentId") {
                        launchSingleTop = true
                    }
                },
                onReservationClick = {
                    navController.navigate("reservations") {
                        launchSingleTop = true
                    }
                },
                onProductsClick = {
                    navController.navigate("products") {
                        launchSingleTop = true
                    }
                },
                onLaundryClick = {
                    navController.navigate("laundry") {
                        launchSingleTop = true
                    }
                },
                onReportsClick = {
                    navController.navigate("reports") {
                        launchSingleTop = true
                    }
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            "stay/{apartmentId}",
            arguments = listOf(navArgument("apartmentId") { type = NavType.LongType })
        ) { backStackEntry ->
            val apartmentId = backStackEntry.arguments?.getLong("apartmentId") ?: return@composable
            StayScreen(
                apartmentId = apartmentId,
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable("products") {
            ProductsScreen(
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable("reservations") {
            ReservationScreen(
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable("history") {
            HistoryScreen(
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable("laundry") {
            LaundryScreen(
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable("reports") {
            ReportsScreen(
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                },
                onHistoryClick = {
                    navController.navigate("history") {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
