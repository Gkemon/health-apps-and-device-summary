package com.gk.emon.allhealthappssummary.presentation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.gk.emon.allhealthappssummary.presentation.AppNavigation.GOOGLE_FIT_SCREEN

object AppNavigation {
    const val GOOGLE_FIT_SCREEN = "google-fit-screen"
    const val HUAWEI_HEALTH_SCREEN = "huawei-health-screen"
    const val HOME_SCREEN = "home-screen"
}

class TodoNavigationActions(private val navController: NavHostController) {

    fun navigateToGoogleFit() {
        navController.navigate(
            GOOGLE_FIT_SCREEN
        ) {
            popUpTo(navController.graph.findStartDestination().id)
            launchSingleTop = true
        }
    }
}