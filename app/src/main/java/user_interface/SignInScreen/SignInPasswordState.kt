package user_interface.SignInScreen

import user_interface.TextFieldState
import kotlin.reflect.KFunction1

/**
 * Holds state of the Password string and performs validation on it along with supplying the
 * relevant error message
 *
 * Author: Renwar Karim
 */

class SignInPasswordState : TextFieldState(
    validator = ::isPasswordValid,
    errorMessage = ::passwordErrorMessage as KFunction1<Any, String>
)

// returns whether the password fulfills constraints or not
fun isPasswordValid(password: String) = password.length >= 3 // : Boolean {
//    if (password.length in 7..15){
//    }
//    return false
// }

// displays password error message
fun passwordErrorMessage(password: String) = "Password Incorrect"
