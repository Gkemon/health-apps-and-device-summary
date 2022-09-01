package com.gk.emon.allhealthappssummary.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gk.emon.allhealthappssummary.presentation.googleFit.GoogleFitScreen
import com.gk.emon.allhealthappssummary.presentation.home.HomeScreen

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppNavigation.HOME_SCREEN,
    navActions: TodoNavigationActions = remember(navController) {
        TodoNavigationActions(navController)
    }
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            AppNavigation.GOOGLE_FIT_SCREEN,
        ) {
            GoogleFitScreen()
        }
        composable(AppNavigation.HOME_SCREEN) {
            HomeScreen { navActions.navigateToGoogleFit() }
        }
    }
}