package user_interface.RegisterScreen

import user_interface.TextFieldState
import kotlin.reflect.KFunction1

class FirstNameState : TextFieldState(
    validator = ::isFirstNameValid,
    errorMessage = ::firstNameErrorMessage as KFunction1<Any, String>
)

// returns whether the input matches that of an int or not
private fun isFirstNameValid(firstName: String): Boolean {
    return firstName != ""
}

// displays error text if isHeightValid function returns false
private fun firstNameErrorMessage(firstName: String) = "Field is empty"
