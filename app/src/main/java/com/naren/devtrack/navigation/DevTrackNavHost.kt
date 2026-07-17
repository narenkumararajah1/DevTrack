package com.naren.devtrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.naren.devtrack.ui.screens.HomeScreen
import com.naren.devtrack.ui.screens.LoginScreen
import com.naren.devtrack.ui.screens.ProjectFormScreen
import com.naren.devtrack.ui.screens.ProjectListScreen
import com.naren.devtrack.ui.screens.RegisterScreen
import com.naren.devtrack.ui.screens.ResetPasswordScreen
import com.naren.devtrack.ui.screens.SplashScreen

@Composable
fun DevTrackNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val onBottomNavigate: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(Screen.Home.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToResetPassword = { navController.navigate(Screen.ResetPassword.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(onNavigate = onBottomNavigate)
        }
        composable(Screen.ProjectList.route) {
            ProjectListScreen(
                onNavigate = onBottomNavigate,
                onNavigateToCreateProject = { navController.navigate(Screen.ProjectForm.createRoute()) },
                onNavigateToEditProject = { projectId ->
                    navController.navigate(Screen.ProjectForm.editRoute(projectId))
                }
            )
        }
        composable(
            route = Screen.ProjectForm.route,
            arguments = listOf(
                navArgument("projectId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            ProjectFormScreen(
                projectId = backStackEntry.arguments?.getString("projectId"),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
