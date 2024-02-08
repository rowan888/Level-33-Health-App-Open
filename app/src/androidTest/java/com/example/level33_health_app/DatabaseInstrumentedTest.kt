package com.example.level33_health_app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.google.firebase.Timestamp
import database.DBCollection
import database.Database
import food.Food
import food.UserFood
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import user.User
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseInstrumentedTest {

    private lateinit var database: Database

    private val food1 = Food(
        "Pepperoni",
        "whole",
        2.0,
        9.0,
        0.87,
        0.0,
        "food_al5qjzna5bpefha4cy7odah0wwt2",
        "http://www.edamam.com/ontologies/edamam.owl#Measure_unit",
        listOf(
            "FAT_FREE",
            "LOW_SUGAR",
            "KETO_FRIENDLY",
            "PALEO",
            "DAIRY_FREE",
            "GLUTEN_FREE",
            "WHEAT_FREE",
            "EGG_FREE",
            "MILK_FREE",
            "PEANUT_FREE",
            "TREE_NUT_FREE",
            "SOY_FREE",
            "FISH_FREE",
            "SHELLFISH_FREE",
            "CRUSTACEAN_FREE",
            "CELERY_FREE",
            "MUSTARD_FREE",
            "SESAME_FREE",
            "LUPINE_FREE",
            "MOLLUSK_FREE",
            "ALCOHOL_FREE",
            "NO_OIL_ADDED",
            "NO_SUGAR_ADDED"
        )
    )

    private val food2 = Food(
        "Beef Pepperoni",
        "serving",
        200.0,
        988.0,
        87.0,
        0.0,
        "food_al5qjzna5bpefha4cy7odah0wwt2",
        "http://www.edamam.com/ontologies/edamam.owl#Measure_serving",
        listOf(
            "LOW_SUGAR",
            "KETO_FRIENDLY",
            "PALEO",
            "DAIRY_FREE",
            "GLUTEN_FREE",
            "WHEAT_FREE",
            "EGG_FREE",
            "MILK_FREE",
            "PEANUT_FREE",
            "TREE_NUT_FREE",
            "SOY_FREE",
            "FISH_FREE",
            "SHELLFISH_FREE",
            "CRUSTACEAN_FREE",
            "CELERY_FREE",
            "MUSTARD_FREE",
            "SESAME_FREE",
            "LUPINE_FREE",
            "MOLLUSK_FREE",
            "ALCOHOL_FREE",
            "NO_OIL_ADDED",
            "NO_SUGAR_ADDED"
        )
    )

    private var user =
        User("Password", "John@smith.com", "John", "Smith", 1.5, 6.0, 0.0, 1.0, 0.0, 2000.0)
    private var user1 =
        User("Password", "John@smith.com", "John", "Smith", 1.5, 6.0, 0.0, 1.0, 0.0, 200.0)
    private var user2 =
        User("Password", "John@smith.com", "Kevin", "Smith", 1.5, 6.0, 0.0, 1.0, 0.0, 200.0)
    private lateinit var testFoodList: MutableList<Food>

    @Before
    fun setUp() {
        user.id = "fScg6yjgcZGzspkbRdbQ"
        user1.id = "2QKuPCoaDqCnOSjUrrQL"
        user2.id = "z1UQF7t6mIsTVHc9tMWw"
        runOnUiThread {
            database = Database()
        }
        testFoodList = mutableListOf(food1, food2)
        database.add(user)
        database.addAll(listOf(user1, user2))
    }

    @After
    fun deleteUsers() {
        database.delete(user.javaClass, user.id)
        database.delete(user1.javaClass, user1.id)
        database.delete(user2.javaClass, user2.id)
    }

    @Test
    fun testUpdateUser() = runBlocking {
        user.firstName = "Sarah"
        database.update(user)
        Assert.assertEquals(user, database.getById(user.javaClass, user.id))
    }

    @Test
    fun testAddAllAndGetAllTestUsers() = runBlocking {
        val userList = mutableListOf(user, user1, user2)
        var getUsers: MutableList<DBCollection>
        runBlocking { getUsers = database.getAllByField(user.javaClass, "email", "John@smith.com") }
        Assert.assertTrue(getUsers.containsAll(userList) && getUsers.size == 3)
    }

    @Test
    fun testAddHasEaten() = runBlocking {
        val userFood = UserFood(
            testFoodList[0].name,
            testFoodList[0].id,
            testFoodList[0].measureUri,
            Timestamp(
                Date(25)
            )
        )
        user.addFoodEaten(userFood)
        database.update(user)
        val retUser = database.getById(user.javaClass, user.id) as User
        Assert.assertEquals(userFood, retUser.hasEaten[0])
    }

    @Test
    fun testAddSearched() = runBlocking {
        user.addSearch("Cookie")
        Assert.assertEquals("Cookie", user.searches[0])
        user.addSearch("Apple")
        user.addSearch("Banana")
        user.addSearch("Apple")
        user.addSearch("Jamaica")
        user.addSearch("Cake")
        Assert.assertEquals(listOf("Cookie", "Apple", "Banana", "Jamaica", "Cake"), user.searches)
        user.addSearch("Home")
        Assert.assertEquals(listOf("Apple", "Banana", "Jamaica", "Cake", "Home"), user.searches)
        database.update(user)
        Assert.assertEquals(
            listOf("Apple", "Banana", "Jamaica", "Cake", "Home"),
            (database.getById(user.javaClass, user.id) as User).searches
        )
    }

    @Test
    suspend fun testUpdateCalorieGoalUser() {
        user.caloriesGoal = 1000.0
        database.update(user)
        Assert.assertEquals(user, database.getById(User().javaClass, user.id) as User)
    }

    @Test
    suspend fun testGetCalorieGoalUser() {
        user.caloriesGoal = 1000.0
        database.update(user)
        Assert.assertEquals(
            user.caloriesGoal,
            (database.getById(User().javaClass, user.id) as User).caloriesGoal,
            0.0
        )
    }

    @Test
    suspend fun testGetIdByEmail() {
        val email = user.email
        Assert.assertEquals(user.id, database.getIdByEmail(email))
    }

    @Test
    suspend fun testIfEmailAlreadyExists() {
        val email = user.email
        Assert.assertTrue("Email already exists", database.checkIfEmailAlreadyExists(email))
    }

    @Test
    suspend fun testUserByEmail() {
        val email = user.email
        Assert.assertEquals(user, database.getUserByEmail(email))
    }

    @Test
    suspend fun testUpdateCalorieIntakeUser() {
        user.caloriesEaten = 200.0
        database.update(user)
        Assert.assertEquals(user, database.getById(User().javaClass, user.id) as User)
    }

    @Test
    suspend fun testGetCalorieIntakeUser() {
        Assert.assertEquals(
            user.caloriesGoal,
            (database.getById(User().javaClass, user.id) as User).caloriesEaten,
            0.0
        )
    }
}
