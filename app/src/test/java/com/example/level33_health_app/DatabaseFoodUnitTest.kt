package com.example.level33_health_app

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import database.Database
import filter_config.FilterConfiguration
import food.Food
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class DatabaseFoodUnitTest {
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

    private lateinit var testFoodList: MutableList<Food>

    @get:Rule
    val instantExecutorRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        database = Database()
        testFoodList = mutableListOf(food1, food2)
    }

    @Test
    fun testUnfilteredGetFood() = runBlocking {
        val foodList = database.getFood("Pepperoni")
        Assert.assertEquals(testFoodList[0], foodList[0])
        Assert.assertEquals(testFoodList[1], foodList[7])
    }

    @Test
    fun testNoList() = runBlocking {
        Assert.assertTrue(database.getFood("zzzzzz").isEmpty())
    }

    @Test
    fun testFilteredGetFood() = runBlocking {
        Assert.assertTrue(database.getFood("Lamb", FilterConfiguration(vegan = true)).isEmpty())
    }

    @Test
    fun testGetOneFoodItem() = runBlocking {
        Assert.assertEquals(
            testFoodList[0],
            database.getSingleFood(
                testFoodList[0].id,
                testFoodList[0].measureUri,
                testFoodList[0].name
            )
        )
    }
}
