package user_interface.ProfileScreen

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import database.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import user_interface.SignInScreen.LogoImage
import java.util.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import loggedIn.LoggedIn
import user_interface.CaloriesScreen.refreshUI
import kotlin.math.roundToInt

/**
 * Profile Screen where the user can view their details, level and stats
 *
 * Author: Rowan Jarvis (Functionality) and Renwar Karim (UI design)
 */

private val levels = HashMap<Int, Int>()
private lateinit var context: Context
private lateinit var db: Database
private val nameState = mutableStateOf("")
private val levelState = mutableStateOf("")
private val expState = mutableStateOf("")

// Displays profile screen and updates the user's current level.
// Contains algorithm for calculating exponential levelling system.
@Composable
fun ProfileScreen(
    navController: NavController
) {

    var counter = 0
    for (i in 1..33) {
        counter += (i * 1000)
        levels[i] = counter
    }

    context = LocalContext.current
    db = Database()
    val scrollState = rememberScrollState()

    getMyLevelBasedOnExperience()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        Row {
            ProfileText()
            Spacer(modifier = Modifier.width(125.dp))
            // re-using the LogoImage composable defined in  SignInScreen.kt
            LogoImage(85)
        }
        HelloText()
        // YourDetailsButton()
        DividerLine()
        YourLevel()
        Spacer(modifier = Modifier.width(10.dp))
        NextLevel(50)
        YourStats(height = LoggedIn.user.height, LoggedIn.user.weight, LoggedIn.user.bmi)
    }
}

@Composable
fun ProfileText() {
    Text(
        "Profile",
        modifier = Modifier.padding(30.dp),
        fontSize = 30.sp,
        color = Color.Black,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun HelloText() {
    val firstName by nameState
    Text(
        "Hello $firstName",
        fontSize = 30.sp
    )
}

// Displays a clickable 'Your Details Button' which then shows the user their details.

@Composable
fun YourDetailsButton() {
    Button(
        onClick = { },
        modifier = Modifier.fillMaxWidth(0.5f)
    ) { Text("Your Details", textAlign = TextAlign.Center) }
}

// Creates line to separate screen
@Composable
fun DividerLine() {
    Divider(
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth(0.9f)
    )
}

// Takes a level integer and displays it.
@Composable
fun YourLevel() {
    val level by levelState
    Text(
        "Your Level",
        fontSize = 30.sp,
        color = Color.Gray
    )
    Text(
        "Level: $level",
        fontSize = 30.sp
    )
}

@Composable
fun NextLevel(value: Int) {
    val level by levelState
    val experience by expState
    var percentage = 0.0

    if (level.isNotEmpty()) {
        if (level == "1") {
            percentage = (experience.toDouble() / 1000) * 100
        } else {
            val prevLevelValue = levels[level.toInt() - 1]!!
            val currLevelValue = level.toInt() * 1000
            val currExp = experience.toDouble() - prevLevelValue
            percentage = (currExp / currLevelValue) * 100
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            "Next level",
            color = Color.Gray,
            fontSize = 15.sp
        )
        Box {
            CustomComponent(
                indicatorValue = percentage.toInt()
            )
            Text(
                "${percentage.toInt()}%",
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun YourStats(height: Double, weight: Double, BMI: Double) {
    Text(
        "Your Stats",
        fontSize = 15.sp,
        color = Color.Gray
    )
    Text("Height: $height cm")
    Text("Weight: $weight kg")
    Text("BMI: ${BMI.roundToInt()}")
}

@Preview(showSystemUi = true)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen(navController = rememberNavController())
}

// Creates level based on user experience.
@SuppressLint("SetTextI18n")
private fun getMyLevelBasedOnExperience() {

    GlobalScope.launch(Dispatchers.Main) {
        val savedUser = LoggedIn.user
        nameState.value = savedUser.firstName

        val experienceInt = savedUser.exp.toString().toDouble()
        // Find my level based on total calories using HashMap levels
        val myLevel = levels.keys.find { levels[it]!! >= experienceInt }
        if (myLevel != null && myLevel <= 33) {
            levelState.value = myLevel.toString()
            expState.value = savedUser.exp.toString()
            savedUser.level = myLevel.toDouble()
            Log.d("FirebaseFirestore", "Level: $myLevel")
        } else {
            savedUser.exp = 0.0
            savedUser.level = 1.0
            savedUser.prestige = savedUser.prestige + 1
            levelState.value = "1"
            expState.value = "0"
        }
        db.update(savedUser)
        refreshUI()
    }
}
