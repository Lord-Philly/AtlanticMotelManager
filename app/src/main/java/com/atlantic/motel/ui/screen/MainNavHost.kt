package com.atlantic.motel.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun MainNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(
                onStayClick = { apartmentId ->
                    navController.navigate("stay/$apartmentId")
                },
                onReservationClick = {
                    navController.navigate("reservations")
                },
                onProductsClick = {
                    navController.navigate("products")
                },
                onLaundryClick = {
                    navController.navigate("laundry")
                },
                onReportsClick = {
                    navController.navigate("reports")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
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
                onBack = { navController.popBackStack() }
            )
        }

        composable("products") {
            ProductsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("reservations") {
            ReservationScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("history") {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("laundry") {
            LaundryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("reports") {
            ReportsScreen(
                onBack = { navController.popBackStack() },
                onHistoryClick = { navController.navigate("history") }
            )
        }
    }
}
