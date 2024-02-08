package user_interface.RegisterScreen

import user_interface.TextFieldState
import java.util.regex.Pattern
import kotlin.reflect.KFunction1

// regex const which matches input string with the format of a double
private const val HEIGHT_VALIDATION_REGEX = "^\\d+.\\d+"

class HeightState : TextFieldState(
    validator = ::isHeightValid,
    errorMessage = ::heightErrorMessage as KFunction1<Any, String>
)

// returns whether the input matches that of an int or not
private fun isHeightValid(height: String): Boolean {
    return Pattern.matches(HEIGHT_VALIDATION_REGEX, height)
}

// displays error text if isHeightValid function returns false
private fun heightErrorMessage(height: String) = "height $height is invalid"
