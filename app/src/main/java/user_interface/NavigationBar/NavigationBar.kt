package user_interface.NavigationBar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.level33_health_app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import loggedIn.LoggedIn
import user.User
import user_interface.Screen
import user_interface.SetupNavGraph

// Function to set up navigation in the app, including the navigation bar and graph
@DelicateCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun SetUpNav(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (currentRoute(navController) != "sign_in_screen" &&
                currentRoute(navController) != "register_screen"
            ) {
                TopBar(scope = scope, scaffoldState = scaffoldState)
            }
        },
        drawerContent = {
            Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
        }
    ) {
        SetupNavGraph(navController = navController)
    }
}

// Function to check what the current route is, returning it as a string
@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

// The menu bar at the top of the screen, allowing for navigation of non sign-in screens
@Composable
fun TopBar(scope: CoroutineScope, scaffoldState: ScaffoldState) {
    TopAppBar(
        title = { Text(text = "Navigation Drawer", fontSize = 18.sp) },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }) {
                Icon(Icons.Filled.Menu, "")
            }
        },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.Black,
    )
}

// The Drawer that opens when you click on the menu
@Composable
fun Drawer(scope: CoroutineScope, scaffoldState: ScaffoldState, navController: NavController) {

    // All items in the navigation menu
    val items = listOf(
        Screen.Search,
        Screen.Profile,
        Screen.Calories
    )
    // The Column of the Drawer, containing the menu
    Column(
        modifier = Modifier.background(color = Color.White)
    ) {
        val backgroudColour = MaterialTheme.colors.primaryVariant
        // The Top Logo of the Menu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(backgroudColour),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.team_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )
        // Code to display and add clickable button to the menu items for navigation
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            DrawerItem(item = item, selected = currentRoute == item.route, onItemClick = {
                navController.navigate(item.route) {
                    navController.graph.startDestinationRoute?.let { route ->
                        popUpTo(route) {
                            saveState = true
                        }
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                scope.launch {
                    scaffoldState.drawerState.close()
                }
            })
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    LoggedIn.user = User()
                    navController.navigate(Screen.SignIn.route)
                    scope.launch { scaffoldState.drawerState.close() }
                }
                .height(45.dp)
                .background(colorResource(id = if (currentRoute == Screen.SignIn.route) R.color.gray else android.R.color.transparent))
                .padding(start = 10.dp)
        ) {
            drawIcon(Icons.Filled.Logout, "Log out")
            Spacer(Modifier.padding(5.dp))
            Text(
                text = "Log Out",
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun drawIcon(icon: ImageVector?, description: String) {
    if (icon != null) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            Modifier.size(16.dp)
        )
    }
}


// The menu items in the drawer in the drawer, taking in the Screen the item is on, whether it's
// been selected, and the button functionality
@Composable
fun DrawerItem(item: Screen, selected: Boolean, onItemClick: (Screen) -> Unit) {
    val background = if (selected) R.color.gray else android.R.color.transparent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item) }
            .height(45.dp)
            .background(colorResource(id = background))
            .padding(start = 10.dp)
    ) {
        drawIcon(item.icon, item.title)
        Spacer(Modifier.padding(5.dp))
        Text(
            text = item.title,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}
