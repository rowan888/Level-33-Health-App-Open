package user_interface

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.DelicateCoroutinesApi
import user_interface.CaloriesScreen.CaloriesScreen
import user_interface.FoodSearcherScreen.FoodSearcherScreen
import user_interface.ProfileScreen.ProfileScreen
import user_interface.RegisterScreen.RegisterScreen
import user_interface.SignInScreen.SignInScreen

@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SignIn.route
    ) {
        composable(
            route = Screen.SignIn.route
        ) {
            // first screen (home screen) set as sign in screen
            SignInScreen(navController = navController)
        }
        composable(
            route = Screen.Register.route
        ) {
            RegisterScreen(navController = navController)
        }
        composable(
            route = Screen.Search.route
        ) {
            FoodSearcherScreen(navController = navController)
        }
        composable(
            route = Screen.Profile.route
        ) {
            ProfileScreen(navController = navController)
        }
        composable(
            route = Screen.Calories.route
        ) {
            CaloriesScreen(navController = navController)
        }
    }
}
