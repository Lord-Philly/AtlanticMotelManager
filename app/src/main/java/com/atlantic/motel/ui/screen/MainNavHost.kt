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

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                onStayClick = { apartmentId ->
                    navController.navigate("stay/$apartmentId")
                },
                onReservationClick = {
                    navController.navigate("reservations")
                },
                onHistoryClick = {
                    navController.navigate("history")
                },
                onProductsClick = {
                    navController.navigate("products")
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
    }
}
