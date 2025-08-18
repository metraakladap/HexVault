package com.metraakladap.hexvault.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.metraakladap.hexvault.screens.LoadingScreen
import com.metraakladap.hexvault.screens.MainScreen
import com.metraakladap.hexvault.screens.OnboardingScreen
import com.metraakladap.hexvault.viewmodel.LoadingScreenViewModel
import com.metraakladap.hexvault.viewmodel.MainViewModel
import com.metraakladap.hexvault.viewmodel.OnboardingViewModel

@Composable
fun NavigationGraph(navController: NavHostController) {


     NavHost(navController, startDestination = Screens.Loading.route) {
         composable(Screens.Loading.route) { backStack ->
             val parent = remember(backStack) {
                 navController.getBackStackEntry(navController.graph.id)
             }
             val viewModel = hiltViewModel<LoadingScreenViewModel>(parent)
             LaunchedEffect(Unit) {
                 viewModel.initLoad { shouldOnboard ->
                     if (shouldOnboard) {
                         navController.navigate(Screens.Onboarding.route) {
                             popUpTo(Screens.Loading.route) { inclusive = true }
                         }
                     } else {
                         navController.navigate(Screens.Main.route) {
                             popUpTo(Screens.Loading.route) { inclusive = true }
                         }
                     }
                 }
             }
             LoadingScreen(viewModel)
         }


         composable(Screens.Main.route) { backStack ->
             val parent = remember(backStack) {
                 navController.getBackStackEntry(navController.graph.id)
             }
             val viewModel = hiltViewModel<MainViewModel>(parent)
             MainScreen(viewModel)
         }

         composable(Screens.Onboarding.route) { backStack ->
             val parent = remember(backStack) {
                 navController.getBackStackEntry(navController.graph.id)
             }
             val viewModel = hiltViewModel<OnboardingViewModel>(parent)
             OnboardingScreen(viewModel) {
                 navController.navigate(Screens.Main.route) {
                     popUpTo(Screens.Loading.route) { inclusive = true }
                 }
             }
         }
     }
}