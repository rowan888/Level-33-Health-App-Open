package user_interface.SignInScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.level33_health_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import user_interface.Screen
import user.User
import database.Database
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.*
import loggedIn.LoggedIn
import java.util.*

// global variables email, password, auth, db
lateinit var email: EmailState
lateinit var password: SignInPasswordState
private lateinit var auth: FirebaseAuth
val db = Database()
lateinit var currentUser: FirebaseUser
lateinit var context: Context

// Sign in screen which acts as the home screen of the app (so user must sign in every time app is
// launched.
@Composable
fun SignInScreen(
    navController: NavController
) {
    auth = Firebase.auth
    context = LocalContext.current
    // puts UI elements in centered alignment in a column arrangement with spacing between elements
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(35.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        LogoImage(150)
        Title()
        SignInText()
        email = remember { EmailState() }
        Email(email.text, email.error) {
            email.text = it
            email.validate()
        }
        password = remember { SignInPasswordState() }
        Password(password.text, password.error) {
            password.text = it
            password.validate()
        }
        // only enable sign in button when both email and password text fields are deemed valid
        SigninButton(enable = email.isValid() && password.isValid(), navController)
        Spacer(modifier = Modifier.size(75.dp))
        // clicking create account tells the navController to navigate the app to register screen
        CreateAccountButton(navController)
    }
}

@Composable
fun LogoImage(size: Int) {
    // loads logo image from resource and sets custom size
    Image(
        painter = painterResource(id = R.drawable.team_logo),
        contentDescription = "App Logo",
        Modifier.size(size.dp)
    )
}

// displays app title name
@Composable
fun Title() {
    Text(
        "Level 33",
        fontSize = 20.sp,
        color = Color.Gray,
        textAlign = TextAlign.Center
    )
}

// displays 'Sign In' text
@Composable
fun SignInText() {
    Text(
        "Sign In",
        fontSize = 30.sp,
        color = Color.Black,
        fontWeight = FontWeight.Bold
    )
}

// takes email input from user and stores as an email that will later be used to find user
// in database, validates the input to ensure it matches the pattern of an email
@Composable
// error can be null i.e. there is no error so we use question mark in String? to declare this
fun Email(email: String, error: String?, onEmailChanged: (String) -> Unit) {
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.7f),
            value = email,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = { onEmailChanged(it) },
            isError = error != null,
            label = { Text("Email", fontSize = 10.sp, color = MaterialTheme.colors.primary) }
        )

        error?.let { ErrorField(it) }
    }
}

// displays error as text with error colour
@Composable
fun ErrorField(error: String) {
    Text(
        text = error,
        modifier = Modifier.fillMaxWidth(0.7f),
        style = TextStyle(color = MaterialTheme.colors.error)
    )
}

// takes password input from user and stores as an email that will later be used to find user
// in database, then validates the field to ensure that its not empty
@Composable
fun Password(password: String, error: String?, onPasswordChanged: (String) -> Unit) {
    var showPassword by remember { mutableStateOf(false) }
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(0.7f),
        value = password,
        onValueChange = { onPasswordChanged(it) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (showPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            if (showPassword) {
                IconButton(onClick = { showPassword = false }) {
                    Icon(
                        imageVector = Icons.Filled.Visibility, // will show the hide password icon
                        // when showPassword is true (its false by default so user would need to
                        // click the show password icon then click the hide password icon for this
                        // to work
                        contentDescription = "Hide Password",
                        Modifier.size(20.dp)
                    )
                }
            } else {
                IconButton(onClick = { showPassword = true }) {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = "Show Password",
                        Modifier.size(20.dp)
                    )
                }
            }
        },
        isError = error != null,
        label = { Text("Password", fontSize = 10.sp, color = MaterialTheme.colors.primary) }
    )

    error?.let { ErrorField(error = it) }
}

fun signIn(navController: NavController) {
    // checks to see if the email is in the database
    auth.signInWithEmailAndPassword(email.text, password.text)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("did work")
                currentUser = auth.currentUser!!
                GlobalScope.launch(Dispatchers.IO) { setUser() }
                navController.navigate(route = Screen.Calories.route)
            } else {
                println("did not work")
                Toast.makeText(
                    context, "Either Email or Password was Incorrect.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}

suspend fun setUser() {
    val user = User()
    LoggedIn.user = db.getById(user.javaClass, currentUser.uid) as User
    val timestamp = Date()
    if (LoggedIn.user.lastLoggedIn.date != timestamp.date) {
        LoggedIn.user.caloriesEaten = 0.0
    }
    LoggedIn.user.lastLoggedIn = timestamp
    db.update(LoggedIn.user)
}

// displays a button which is only enabled when input validation is successfully passed
@DelicateCoroutinesApi
@Composable
fun SigninButton(enable: Boolean, navController: NavController) {
    Button(
        onClick = {
            signIn(navController)
            // take user to homepage
            // signIn(navController, email.text, password.text)
        },
        modifier = Modifier.fillMaxWidth(0.7f),
        enabled = enable
    ) { Text("Sign in", textAlign = TextAlign.Center) }
}

// displays a create account text which is clickable, acting like a button to navigate to register
@Composable
fun CreateAccountButton(navController: NavController) {
    ClickableText(
        text = AnnotatedString("Create Account"),
        onClick = {
            navController.navigate(route = Screen.Register.route)
        },
        style = TextStyle(color = Color.Gray)
    )
}
