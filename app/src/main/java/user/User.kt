package user

import database.DBCollection
import food.UserFood
import java.util.*

/**
 * Class to hold the User and their associated details, all of which is stored on Firestore database
 * Contains: ID, Password, Email, First Name, Last Name, Height, Weight, Prestige, Level, EXP,
 * Calories Eaten, List of Searches, List of Food Eaten, and List of Achievements
 *
 * Author: Alexander Fall
 */

class User(
    password: String,
    email: String,
    firstName: String,
    lastName: String,
    height: Double,
    weight: Double,
    prestige: Double,
    level: Double,
    exp: Double,
    caloriesEaten: Double,
    caloriesGoal: Double = 0.0
) : DBCollection() {
    override var id = ""
        get() = field
        set(value) {
            field = value
        }
    var password = password
        get() = field
        set(value) {
            field = value
        }
    var email = email
        get() = field
        set(value) {
            field = value
        }
    var firstName = firstName
        get() = field
        set(value) {
            field = value
        }
    var lastName = lastName
        get() = field
        set(value) {
            field = value
        }
    var height = height
        get() = field
        set(value) {
            field = value
        }
    var weight = weight
        get() = field
        set(value) {
            field = value
        }
    var bmi = 0.0
        get() = field
        set(value) {
            field = value
        }
    var prestige = prestige
        get() = field
        set(value) {
            field = value
        }
    var level = level
        get() = field
        set(value) {
            field = value
        }
    var exp = exp
        get() = field
        set(value) {
            field = value
        }
    var caloriesEaten = caloriesEaten
        get() = field
        set(value) {
            field = value
        }
    var caloriesGoal = caloriesGoal
        get() = field
        set(value) {
            field = value
        }
    var lastLoggedIn = Date()
    var achievements = hashMapOf<String, Boolean>("1" to false, "2" to false)
        get() = field
    var searches = mutableListOf<String>()
        get() = field
    var hasEaten = mutableListOf<UserFood>()
        get() = field

    fun addFoodEaten(food: UserFood) {
        hasEaten.add(food)
    }

    fun removeFoodEaten(food: UserFood) {
        if (food in hasEaten) hasEaten.remove(food)
    }

    fun addSearch(search: String) {
        if (search !in searches) {
            if (searches.size == 5) {
                searches.removeAt(0)
            }
            searches.add(search)
        }
    }

    fun removeSearch(search: String) {
        if (searches.contains(search)) {
            searches.remove(search)
        }
    }

    fun hasAchievement(i: String): Boolean {
        return if (achievements[i] != null) {
            achievements[i]!!
        } else {
            false
        }
    }

    fun setAchievement(i: String, newVal: Boolean) {
        if (achievements[i] != null) {
            achievements[i] = newVal
        }
    }

    fun addPrestige(i: Int) {
        prestige += i
    }

    fun addLevel(i: Int) {
        level += i
    }

    fun addExp(i: Int) {
        exp += i
    }

    fun addCalEaten(i: Int) {
        caloriesEaten += i
    }

    override fun toString(): String {
        return "User(id='$id', password='$password', email='$email', firstName='$firstName', lastName='$lastName', height=$height, weight=$weight, bmi=$bmi prestige=$prestige, level=$level, exp=$exp)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (password != other.password) return false
        if (email != other.email) return false
        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (height != other.height) return false
        if (weight != other.weight) return false
        if (prestige != other.prestige) return false
        if (level != other.level) return false
        if (exp != other.exp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + weight.hashCode()
        result = 31 * result + prestige.hashCode()
        result = 31 * result + level.hashCode()
        result = 31 * result + exp.hashCode()
        return result
    }

    // Blank constructor
    constructor() : this("", "", "", "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
}
