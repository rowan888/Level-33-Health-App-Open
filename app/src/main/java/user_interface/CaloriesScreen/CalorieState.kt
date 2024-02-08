package user_interface.CaloriesScreen

import user_interface.TextFieldState
import utils.isNumber
import kotlin.reflect.KFunction1

/**
 * File that stores functions related to the users Calorie states
 *
 * Author: Rowan Jarvis (Functionality and UI design)
 */

class CalorieState : TextFieldState(
    validator = ::isCalorieValid,
    errorMessage = ::calorieErrorMessage as KFunction1<Any, String>
)

fun isCalorieValid(calorie: String): Boolean {
    return calorie.isNumber()
}

fun calorieErrorMessage(calorie: String) = "Not a valid number"
