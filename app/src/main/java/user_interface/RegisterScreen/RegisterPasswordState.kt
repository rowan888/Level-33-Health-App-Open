package user_interface.RegisterScreen

import user_interface.TextFieldState
import kotlin.reflect.KFunction1

// regex constant that matches password constraints
private const val PASSWORD_VALIDATION_REGEX =
    "^(?=.*\\\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!*@#\$%]).{7,15}\$"

class RegisterPasswordState : TextFieldState(
    validator = ::isPasswordValid,
    errorMessage = ::passwordErrorMessage as KFunction1<Any, String>
)

// returns whether the password fulfills constraints or not
fun isPasswordValid(password: String): Boolean {
    if (password.length in 7..15) {
        return true // Pattern.matches(PASSWORD_VALIDATION_REGEX, password)
    }
    return false
}

// displays password error message
fun passwordErrorMessage(password: String) =
    "Passwords must have at least one special character, one number, one" +
            " upper case letter, one lower case letter and must be between 7-15 characters long"
