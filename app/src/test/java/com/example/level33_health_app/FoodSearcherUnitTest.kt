package com.example.level33_health_app

// import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import database.Database
import filter_config.FilterConfiguration
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import process_layer.food_searcher.FoodSearcher
import user.User
import food.AFood

class FoodSearcherUnitTest {

    private lateinit var database: Database
    private lateinit var searcher: FoodSearcher
    private lateinit var config: FilterConfiguration
    private lateinit var user: User
    val search = "Test Search"

    @Before
    fun setUp() {
        database = Database()
        config = FilterConfiguration()
        searcher = FoodSearcher(config)
        user = User("Password", "John@smith.com", "John", "Smith", 1.5, 6.0, 0.0, 1.0, 0.0, 2000.0)
        user.addSearch(search)

        // only exists temporarily
        searcher.currentUser = user
    }

    @After
    fun deleteUser() {
        database.delete(user.javaClass, user.id)
    }

    @After
    fun deleteUsers() {
        database.delete(user.javaClass, user.id)
    }

    @Test
    fun testSearchWithFiltersCorrectResults() = runBlocking {
        var testFailure = false

        // testing the ranges
        val rangeConfig = FilterConfiguration(400, 200, 20, 10, 20, 0)
        searcher.setFilterConfiguration(rangeConfig)
        var results: List<AFood> = searcher.search("beef")

        for (food: AFood in results) {
            if (food.calories !in 400.0..200.0) {
                testFailure = true
            }
            if (food.fat !in 20.0..10.0) {
                testFailure = true
            }
            if (food.fat !in 20.0..0.0) {
                testFailure = true
            }
        }
        assertFalse(testFailure)

        // now testing the health labels
        val healthConfig = FilterConfiguration(alcoholFree = true, eggFree = true, fishFree = true, porkFree = true, mustardFree = true)
        searcher.setFilterConfiguration(healthConfig)
        results = searcher.search("sausage")

        for (food: AFood in results) {
            if (!food.healthLabels.containsAll(
                    listOf(
                    "alcohol-free",
                    "egg-free",
                    "fish-free",
                    "pork-free",
                    "mustard-free"
                )
                )
            ) {
                testFailure = true
            }
        }
        assertFalse(testFailure)
    }

    @Test
    fun testAutoCompleteResults() = runBlocking {
        assertTrue(searcher.searchSuggest("eg").isNotEmpty())
    }

    @Test
    fun testEmptyAutocompletePrevSearches() = runBlocking {
        searcher.search("search 2")

        assertArrayEquals(searcher.getPrevSearches().toTypedArray(), searcher.searchSuggest("").toTypedArray())
    }

    @Test
    fun testPreviousSearchSuggestions() = runBlocking {

        assertTrue(searcher.getPrevSearches().size == 1)
        assertTrue(searcher.getPrevSearches().contains(search))

        database.add(user)
        searcher.search("hello")

        assertTrue(searcher.getPrevSearches().size == 2)
        assertTrue(
            searcher.getPrevSearches().contains(search) and searcher.getPrevSearches().contains("hello")
        )
    }

    @Test
    fun testSetFilterConfiguration() {

        val newConfig = FilterConfiguration(9, minKcal = 8)
        searcher.setFilterConfiguration(newConfig)
        assertNotSame(config, newConfig)
    }

    @Test
    fun testSearchAddedToSearchHistory() = runBlocking {
        database.add(user)
        searcher.search("hello")

        val dbUser: User = database.getById(user.javaClass, user.id) as User

        assertTrue(user.searches.contains("hello"))
        assertTrue(dbUser.searches.contains("hello"))
    }

    @Test
    fun testRemoveSearchInvalid() = runBlocking {
        database.add(user)
        searcher.removeSearch("invalid")

        val dbUser: User = database.getById(user.javaClass, user.id) as User

        assertTrue(user.searches.size == 1)
        assertTrue(dbUser.searches.size == 1)
    }

    @Test fun testRemoveSearch() = runBlocking {
        database.add(user)
        searcher.removeSearch(search)

        val dbUser: User = database.getById(user.javaClass, user.id) as User

        assertTrue(user.searches.size == 0)
        assertTrue(dbUser.searches.size == 0)
    }

    @Test fun testRemoveAllPreviousSearch() = runBlocking {
        database.add(user)

        // single search
        searcher.removeSearchHistory()

        var dbUser: User = database.getById(user.javaClass, user.id) as User

        assertTrue(user.searches.size == 0)
        assertTrue(dbUser.searches.size == 0)

        // multiple searches
        user.addSearch("1")
        user.addSearch("two")
        searcher.removeSearchHistory()

        dbUser = database.getById(user.javaClass, user.id) as User

        assertTrue(user.searches.size == 0)
        assertTrue(dbUser.searches.size == 0)
    }

    @Test
    fun testSearchSuggestProducesSuggestions() = runBlocking {
        searcher.removeSearchHistory()
        assertTrue(searcher.searchSuggest("a").isNotEmpty())
    }
}
