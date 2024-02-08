package user_interface

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon:ImageVector? ) {
    // define all screens in the ap as object of Screen class with a route name and title
    object SignIn : Screen(route = "sign_in_screen", title = "", icon = null)
    object Register : Screen(route = "register_screen", title = "", icon = null)
    object Search : Screen("food_searcher", title = "Food Search", icon = Icons.Filled.Search)
    object Profile : Screen("ProfileScreen", "Profile", icon = Icons.Filled.AccountCircle)
    object Calories : Screen("CaloriesScreen", "Calories", icon = Icons.Filled.Restaurant)
}
