package com.example.level33_health_app

import org.junit.Test

import org.junit.Assert.*
import user_interface.CaloriesScreen.calorieErrorMessage
import user_interface.CaloriesScreen.isCalorieValid
import user_interface.SignInScreen.*
import user_interface.SignInScreen.emailErrorMessage
import user_interface.SignInScreen.isEmailValid
import user_interface.SignInScreen.isPasswordValid
import user_interface.SignInScreen.passwordErrorMessage

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class LoginCalorieUnitTest {

    @Test
    fun email_isCorrect() {
        val email = "test@gmail.com"
        assertTrue(emailErrorMessage(email = email), isEmailValid(email))
    }

    @Test
    fun email_empty_isWrong() {
        val email = ""
        assertFalse(emailErrorMessage(email = email), isEmailValid(email))
    }

    @Test
    fun email_invalid_isWrong() {
        val email = "test"
        assertFalse(emailErrorMessage(email = email), isEmailValid(email))
    }

    @Test
    fun password_isCorrect() {
        val password = "123456"
        assertTrue(passwordErrorMessage(password), isPasswordValid(password))
    }

    @Test
    fun calorie_isCorrect() {
        val calValue = "1000"
        assertTrue(calorieErrorMessage(calValue), isCalorieValid(calValue))
    }

    @Test
    fun calorie_isWrong() {
        val calValue = "asd"
        assertFalse(calorieErrorMessage(calValue), isCalorieValid(calValue))
    }}
