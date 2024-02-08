package user_interface.RegisterScreen

import user_interface.TextFieldState
import java.util.regex.Pattern
import kotlin.reflect.KFunction1

// regex const which matches input string with the format of a double
private const val WEIGHT_VALIDATION_REGEX = "^\\d+.\\d+"

class WeightState : TextFieldState(
    validator = ::isWeightValid,
    errorMessage = ::weightErrorMessage as KFunction1<Any, String>
)

// returns whether the input matches that of an int or not
private fun isWeightValid(weight: String): Boolean {
    return Pattern.matches(WEIGHT_VALIDATION_REGEX, weight)
}

// displays error text if isWeightValid function returns false
private fun weightErrorMessage(weight: String) = "height $weight is invalid"
