package user_interface.RegisterScreen

import user_interface.TextFieldState
import kotlin.reflect.KFunction1

class LastNameState : TextFieldState(
    validator = ::isLastNameValid,
    errorMessage = ::lastNameErrorMessage as KFunction1<Any, String>
)

// returns whether the input matches that of an int or not
private fun isLastNameValid(lastName: String): Boolean {
    return lastName != ""
}

// displays error text if isHeightValid function returns false
private fun lastNameErrorMessage(lastName: String) = "Field is empty"
