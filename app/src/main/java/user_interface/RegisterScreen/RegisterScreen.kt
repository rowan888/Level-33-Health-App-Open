package user_interface.RegisterScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import database.Database
import user.User
import user_interface.Screen
import user_interface.SignInScreen.EmailState
import user_interface.SignInScreen.context

private lateinit var auth: FirebaseAuth
lateinit var height: HeightState
lateinit var weight: WeightState
lateinit var email: EmailState
lateinit var password: RegisterPasswordState
lateinit var firstName: FirstNameState
lateinit var lastName: LastNameState
lateinit var context: Context

// Function which displays register screen in app
@Composable
fun RegisterScreen(
    navController: NavController
) {
    auth = Firebase.auth
    context = LocalContext.current
    val scrollState = rememberScrollState()
    // main column that displays the composable elements on screen
    Column {
        SignUpText()
        // puts UI elements in centered alignment in a column arrangement with spacing
        // between elements
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(35.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            email = remember { EmailState() }
            Email(email.text, email.error) {
                email.text = it
                email.validate()
            }
            firstName = remember { FirstNameState() }
            FirstName(firstName.text, firstName.error) {
                firstName.text = it
                firstName.validate()
            }
            lastName = remember { LastNameState() }
            LastName(lastName.text, lastName.error) {
                lastName.text = it
                lastName.validate()
            }
            height = remember { HeightState() }
            Height(height.text, height.error) {
                height.text = it
                height.validate()
            }
            weight = remember { WeightState() }
            Weight(weight.text, weight.error) {
                weight.text = it
                weight.validate()
            }
            password = remember { RegisterPasswordState() }
            Password(password.text, password.error) {
                password.text = it
                password.validate()
            }
            ConfirmPassword()
            Spacer(modifier = Modifier.size(10.dp))
            SignUpButton(
                email = email.text,
                firstName = firstName.text,
                lastName = lastName.text,
                password = password.text,
                navController = navController
            )
        }
    }
}

// function to take a users height and input it to database
@Composable
// error can be null i.e. there is no error so we use question mark in String? to declare this
fun Height(height: String, error: String?, onHeightChanged: (String) -> Unit) {
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.7f),
            value = height,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { onHeightChanged(it) },
            isError = error != null,
            label = { Text("Height (e.g 1.8m)", fontSize = 12.sp, color = MaterialTheme.colors.primary) }
        )

        error?.let { user_interface.SignInScreen.ErrorField(it) }
    }
}

// function to take a users weight and input it to database
@Composable
// error can be null i.e. there is no error so we use question mark in String? to declare this
fun Weight(weight: String, error: String?, onHeightChanged: (String) -> Unit) {
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.7f),
            value = weight,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { onHeightChanged(it) },
            isError = error != null,
            label = { Text("Weight (e.g. 81.Kg)", fontSize = 12.sp, color = MaterialTheme.colors.primary) }
        )

        error?.let { user_interface.SignInScreen.ErrorField(it) }
    }
}

// displays the sign up text
@Composable
fun SignUpText() {
    Text(
        "Sign Up",
        modifier = Modifier.padding(30.dp),
        fontSize = 30.sp,
        color = Color.Black,
        fontWeight = FontWeight.Bold
    )
}

// Takes email input from user and saves it
@Composable
fun Email(email: String, error: String?, onEmailChanged: (String) -> Unit) {
    Column {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .border(2.dp, color = MaterialTheme.colors.primary),
            value = email,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = { onEmailChanged(it) },
            label = {
                Text(
                    text = "Email",
                    fontSize = 12.sp,
                    color = MaterialTheme.colors.primary
                )
            },
            isError = error != null
        )

        error?.let { ErrorField(it) }
    }
}

@Composable
fun ErrorField(error: String) {
    Text(
        text = error,
        modifier = Modifier.fillMaxWidth(0.7f),
        // style = TextStyle(color = MaterialTheme.colors.error)
    )
}

// Takes first name input from user and saves it
@Composable
fun FirstName(firstName: String, error: String?, onFirstNameChanged: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(0.7f),
        value = firstName,
        singleLine = true,
        onValueChange = { onFirstNameChanged(it) },
        label = {
            Text(
                text = "First Name",
                fontSize = 12.sp,
                color = MaterialTheme.colors.primary
            )
        },
        isError = error != null
    )
    error?.let { ErrorField(it) }
}

// Takes last name input from user and saves it
@Composable
fun LastName(lastName: String, error: String?, onLastNameChanged: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(0.7f),
        value = lastName,
        singleLine = true,
        onValueChange = { onLastNameChanged(it) },
        label = {
            Text(
                text = "Last Name",
                fontSize = 12.sp,
                color = MaterialTheme.colors.primary
            )
        },
        isError = error != null
    )
    error?.let { ErrorField(it) }
}

// Takes password input from user and saves it
@Composable
fun Password(password: String, error: String?, onPasswordChanged: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(0.7f),
        value = password,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        onValueChange = { onPasswordChanged(it) },
        label = { Text(text = "Password", fontSize = 12.sp, color = MaterialTheme.colors.primary) },
        isError = error != null
    )

    error?.let { ErrorField(it) }
}

// Takes password input again from user and ensures that both passwords match
@Composable
fun ConfirmPassword() {
    // var password = remember {PasswordState()}
    var confirmPassword by remember { mutableStateOf("") }
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(0.7f),
        value = confirmPassword,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        onValueChange = { confirmPassword = it },
        label = {
            Text(
                text = "Confirm Password",
                fontSize = 12.sp,
                color = MaterialTheme.colors.primary
            )
        },
    )
}

// displays clickable sign up button
@Composable
fun SignUpButton(
    email: String,
    firstName: String,
    lastName: String,
    password: String,
    navController: NavController
) {
    Button(
        onClick = {
            val userBMI = weight.text.toDouble() / (height.text.toDouble() * height.text.toDouble())
            var db = Database()
            var user = User(
                password,
                email,
                firstName,
                lastName,
                height.text.toDouble(),
                weight.text.toDouble(),
                0.0,
                1.0,
                0.0,
                0.0
            )
            user.bmi = userBMI
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Successfully made account")
                    Toast.makeText(
                        context, "Correctly made account.",
                        Toast.LENGTH_SHORT
                    ).show()
                    user.id = auth.uid.toString()
                    db.add(user)
                    navController.navigate(route = Screen.SignIn.route)
                }
            }
        },
        modifier = Modifier.fillMaxWidth(0.7f)
    ) { Text("Continue", textAlign = TextAlign.Center) }
}

@Preview(showSystemUi = true)
@Composable
fun RegisterScreenPreview(){
    RegisterScreen(navController = rememberNavController())
}
