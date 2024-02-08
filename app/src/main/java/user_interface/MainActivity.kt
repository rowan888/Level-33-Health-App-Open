package user_interface

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import user_interface.NavigationBar.SetUpNav
import user_interface.ui.theme.Level33HealthAppTheme

/**
 * This is the main class where the app is launched from, it initializes values and sets up the
 * navigation controller which allows navigation between screens in the app
 *
 * Author: Renwar Karim
 */
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {

    lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Level33HealthAppTheme {
                navController = rememberNavController()
                SetUpNav(navController = navController)
            }
        }
    }
}
