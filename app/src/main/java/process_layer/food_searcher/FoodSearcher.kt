// Author - Lucas Toner

package process_layer.food_searcher
import android.util.Log

import database.Database
import food.AFood
import filter_config.FilterConfiguration
import food.Food
import food.Meal
import user.User

class FoodSearcher(private var config: FilterConfiguration) {

    var currentUser = loggedIn.LoggedIn.user

    val db = Database()

    // Sets a new filterConfig object
    // Inputs: The new filterConfig Object
    fun setFilterConfiguration(newConfig: FilterConfiguration) {
        config = newConfig
        Log.e("config", config.ingredient.toString())
    }

    // Performs the search with the chosen filters and search term
    // Inputs: Search Term
    // Returns: List of filtered Food objects
    public suspend fun search(term: String): MutableList<AFood> {
        currentUser.addSearch(term)
        db.update(currentUser)
        Log.e("search", this.config.ingredient.toString())
        return(db.getFood(term, this.config))
    }

    // To be run whenever the user makes a change in the search box
    // Inputs: The input in the search box
    // Returns: A string list of term suggestions
    public suspend fun searchSuggest(term: String): List<String> {
        if (term.isEmpty()) {
            return getPrevSearches()
        } else {
            return getAutocompSuggestions(term)
        }
    }

    // returns a list of the most recent previous searches
    public fun getPrevSearches(): List<String> {
        return currentUser.searches
    }

    // Returns a list of autocomplete suggestions
    // Input: The term to complete
    // Returns: A string list of term suggestions
    public suspend fun getAutocompSuggestions(term: String): List<String> {
        return db.getFoodSuggestions(term)
    }

    // Displays the foodViewer dialog to the user
    // Input: The selected food
    private fun selectFood(food: AFood) {
        when (food) {
            is Food -> {
                val viewer = MealViewer(food)
                viewer.display()
            }
            is Meal -> {
                val viewer = FoodViewer(food)
                viewer.display()
            }
        }
    }

    // Removes a specific search from a user's profile based on inputted text
    // Input: The body of the search
    public suspend fun removeSearch(text: String) {
        currentUser.removeSearch(text)
        db.update(currentUser)
    }

    // Deletes all search history from a users profile
    public suspend fun removeSearchHistory() {
        currentUser.searches.clear()
        db.update(currentUser)
    }
}
