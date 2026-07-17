package com.naren.devtrack.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ResetPassword : Screen("reset_password")
    object Home : Screen("home")
    object ProjectList : Screen("projects")
    object ProjectForm : Screen("project_form?projectId={projectId}") {
        fun createRoute() = "project_form"
        fun editRoute(projectId: String) = "project_form?projectId=$projectId"
    }
}
