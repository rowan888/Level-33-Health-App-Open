package user_interface.CaloriesScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import database.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import loggedIn.LoggedIn
import user_interface.Screen
import utils.Auth.authUser
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

private lateinit var context: Context
private val targetCalState = mutableStateOf("")
private val intakeCalState = mutableStateOf("")
private lateinit var db: Database

/**
 * Calories Screen where the user can view and change their daily calorie goal. User can also add calories to total.
 *
 * Author: Rowan Jarvis (Functionality and UI design)
 */

@Composable
fun CaloriesScreen(
    navController: NavController
) {
    context = LocalContext.current
    db = Database()

    // Get User Daily Calorie Goal
    getUserCalorieGoal()

    // Get Total Calories
    getTotalCalories()

    Column {
        // Puts UI elements in centered alignment in a column arrangement with spacing between elements
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CalorieCounterText()
            TotalCaloriesText()
            TargetCaloriesText()
            RemainingCaloriesText()
            ProgressCalories()
            val calorieGoal = remember { CalorieState() }
            TotalCalories(calorieGoal.text) {
                calorieGoal.text = it
                calorieGoal.validate()
            }
            TotalCaloriesButton(calorieGoal)
            val calorieIntake = remember { CalorieState() }
            IntakeCalories(calorieIntake.text) {
                calorieIntake.text = it
                calorieIntake.validate()
            }
            IntakeCaloriesButton(calorieIntake)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    CaloriesScreen(navController = rememberNavController())
}

@Composable
fun CalorieCounterText() {
    Text(
        "Calorie Counter",
        modifier = Modifier.padding(5.dp),
        fontSize = 23.sp,
        color = Color.Black,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun TotalCaloriesText() {
    val text by intakeCalState
    Text(
        "Current Calories: $text",
        modifier = Modifier.padding(5.dp),
        fontSize = 14.sp,
    )
}

@Composable
fun TargetCaloriesText() {
    val text by targetCalState
    Text(
        "Target Calories: $text",
        modifier = Modifier.padding(5.dp),
        fontSize = 14.sp,
    )
}

@Composable
fun RemainingCaloriesText() {
    val intaked by intakeCalState
    val target by targetCalState
    val intakedValue = if (intaked.isEmpty()) 0.0 else intaked.toDouble()
    val targetValue = if (target.isEmpty()) 0.0 else target.toDouble()
    Text(
        "Calories to go: ${max(0.0, targetValue - intakedValue)}",
        modifier = Modifier.padding(5.dp),
        fontSize = 14.sp,
    )
}

@Composable
fun ProgressCalories() {
    val intaked by intakeCalState
    val target by targetCalState
    val intakedValue = if (intaked.isEmpty()) 0.0 else intaked.toDouble()
    val targetValue = if (target.isEmpty()) 0.0 else target.toDouble()
    var progress = 0
    if (intakedValue >= targetValue) {
        progress = 100
    } else {
        progress = ((intakedValue / targetValue) * 100).toInt()
    }
    Box(modifier = Modifier.defaultMinSize()) {
        CircularProgressIndicator(
            progress = 1f,
            color = Color.LightGray,
            modifier = Modifier.size(70.dp),
            strokeWidth = 5.dp
        )
        CircularProgressIndicator(
            progress = (progress.toDouble() / 100).toFloat(),
            color = Color.Blue,
            modifier = Modifier.size(70.dp),
            strokeWidth = 5.dp
        )
        Text(
            "$progress%",
            fontSize = 16.sp,
            color = Color.Blue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun TotalCalories(calGoals: String, onGoalChanged: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(0.7f),
        value = calGoals,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = { onGoalChanged(it) },
        label = { Text(text = "Total Calories", color = MaterialTheme.colors.primary) }
    )
}

@Composable
fun TotalCaloriesButton(calGoals: CalorieState) {
    Button(
        onClick = {
            if (calGoals.isValid()) {
                updateUserCalorieGoal(calGoals.text.toDouble())
            } else {
                Toast.makeText(context, calGoals.error, Toast.LENGTH_SHORT).show()
            }
        },
        modifier = Modifier.fillMaxWidth(0.7f)
    ) { Text("Continue", textAlign = TextAlign.Center) }
}

@Composable
fun IntakeCalories(calIntake: String, onIntakeChanged: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(0.7f),
        value = calIntake,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = { onIntakeChanged(it) },
        label = { Text(text = "Intake Calories", color = MaterialTheme.colors.primary) }
    )
}

@Composable
fun IntakeCaloriesButton(calorieIntake: CalorieState) {
    Button(
        onClick = {
            if (calorieIntake.isValid()) {
                // Check if the user has already inserted the daily calorie.
                dailyCalorieIntakedLogged(calorieIntake.text)
            } else {
                Toast.makeText(context, "Please insert a value", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = Modifier.fillMaxWidth(0.7f)
    ) { Text("Continue", textAlign = TextAlign.Center) }
}

// Function to log the users daily calorie intake.
fun dailyCalorieIntakedLogged(dailyCalorieIntake: String) {
    val db = FirebaseFirestore.getInstance()
    val userId = LoggedIn.user.id
    val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

    db.collection("users").document(userId).get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val calorieIntake = document.get("caloriesEaten")
                if (calorieIntake != null) {
                    val calorieIntakeInt = calorieIntake.toString().toDouble()
                    var newCalorieDaily = calorieIntakeInt + dailyCalorieIntake.toDouble()
                    db.collection("users").document(userId).update("caloriesEaten", newCalorieDaily)
                    checkIfUserBeatGoal(newCalorieDaily)
                    updateTotalCaloriesByUser(newCalorieDaily)
                } else {
                    db.collection("users").document(userId).update("caloriesEaten", dailyCalorieIntake)
                    checkIfUserBeatGoal(dailyCalorieIntake.toDouble())
                    updateTotalCaloriesByUser(dailyCalorieIntake.toDouble())
                }
            } else {
                //  Toast.makeText(this, "No such document", Toast.LENGTH_SHORT).show()
            }
        }
}

// Function to update the users calorie goal.
private fun updateUserCalorieGoal(newCalorieGoal: Double) {

    GlobalScope.launch(Dispatchers.Main) {
        val savedUser = LoggedIn.user
        savedUser.caloriesGoal = newCalorieGoal
        db.update(savedUser)
        authUser = savedUser
        refreshUI()
    }
}

// Function to get the users calorie goal data.
private fun getUserCalorieGoal() {
    GlobalScope.launch(Dispatchers.Main) {
        val savedUser = LoggedIn.user
        targetCalState.value = savedUser.caloriesGoal.toString()
    }
}

// Function to add experience to users level (For future implementation).
fun addUserExperienceBonus(experience: Int) {
    GlobalScope.launch(Dispatchers.Main) {
        val savedUser = LoggedIn.user
        savedUser.addExp(experience)
        db.update(savedUser)
        authUser = savedUser
        refreshUI()
    }
}

// Function to fresh UI with new data.
fun refreshUI() {
    getUserCalorieGoal()
    getTotalCalories()
}

// Function to check if user beat their calorie goal. If true then adds experience too level.
private fun checkIfUserBeatGoal(dailyCalorieIntake: Double) {
    GlobalScope.launch(Dispatchers.Main) {
        val savedUser = LoggedIn.user
        if (dailyCalorieIntake >= savedUser.caloriesGoal) {
            savedUser.addExp(300)
            Toast.makeText(
                user_interface.SignInScreen.context, "Congrats on reaching your goal! 200 experience added!",
                Toast.LENGTH_SHORT
            ).show()
            db.update(savedUser)
            refreshUI()
        }
    }
}

// Function to update the users total calories.
private fun updateTotalCaloriesByUser(dailyCalorieIntake: Double) {
    GlobalScope.launch(Dispatchers.Main) {
        val savedUser = LoggedIn.user
        savedUser.caloriesEaten = dailyCalorieIntake
        db.update(savedUser)
        authUser = savedUser
        refreshUI()
    }
}

private fun getTotalCalories() {
    GlobalScope.launch(Dispatchers.Main) {
        val savedUser = LoggedIn.user
        intakeCalState.value = savedUser.caloriesEaten.toString()
    }
}
