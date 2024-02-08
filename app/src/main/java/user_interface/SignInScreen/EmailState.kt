package user_interface.SignInScreen

import user_interface.TextFieldState
import java.util.regex.Pattern
import kotlin.reflect.KFunction1

// regex const which matches input string with the format of an email
private const val EMAIL_VALIDATION_REGEX = "^(.+)@(.+)\$"

/**
 * Holds state of the Email string and performs validation on it along with supplying the
 * relevant error message
 *
 * Author: Renwar Karim
 */
class EmailState : TextFieldState(
    validator = ::isEmailValid,
    errorMessage = ::emailErrorMessage as KFunction1<Any, String>
)

// returns whether the string matches that of an email or not
fun isEmailValid(email: String): Boolean {
    return Pattern.matches(EMAIL_VALIDATION_REGEX, email)
}

// displays error text if isEmailValid function returns false
fun emailErrorMessage(email: String) = "Email $email is invalid"
