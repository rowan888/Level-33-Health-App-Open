package user_interface.FoodSearcherScreen

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import database.Database
import filter_config.FilterConfiguration
import food.AFood
import food.Meal
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import process_layer.food_searcher.FoodSearcher
import process_layer.food_searcher.MealViewer

@DelicateCoroutinesApi
class FoodSearcherViewModel : ViewModel() {

    var searcher: FoodSearcher = FoodSearcher(FilterConfiguration())

    val results: MutableState<List<AFood>> = mutableStateOf(ArrayList())
    val loading = mutableStateOf(false)
    val selectedFood: MutableState<AFood?> = mutableStateOf(null)

    val suggestions: MutableState<List<String>> = mutableStateOf(ArrayList())

    val ingredient = mutableStateOf(true)

    val alcoholFree = mutableStateOf(false)
    val celeryFree = mutableStateOf(false)
    val crustaceanFree = mutableStateOf(false)
    val dairyFree = mutableStateOf(false)
    val eggFree = mutableStateOf(false)
    val fishFree = mutableStateOf(false)
    val glutenFree = mutableStateOf(false)
    val keto = mutableStateOf(false)
    val kosher = mutableStateOf(false)
    val lupineFree = mutableStateOf(false)
    val mustardFree = mutableStateOf(false)
    val paleo = mutableStateOf(false)
    val peanutFree = mutableStateOf(false)
    val porkFree = mutableStateOf(false)
    val redMeatFree = mutableStateOf(false)
    val sesameFree = mutableStateOf(false)
    val soyFree = mutableStateOf(false)
    val treeNutFree = mutableStateOf(false)
    val vegan = mutableStateOf(false)
    val pescatarian = mutableStateOf(false)
    val vegetarian = mutableStateOf(false)
    val wheatFree = mutableStateOf(false)

    val maxKcal = mutableStateOf(Int.MAX_VALUE)
    val minKcal = mutableStateOf(Int.MIN_VALUE)
    val maxFat = mutableStateOf(Int.MAX_VALUE)
    val minFat = mutableStateOf(Int.MIN_VALUE)
    val maxSugar = mutableStateOf(Int.MAX_VALUE)
    val minSugar = mutableStateOf(Int.MIN_VALUE)

    @DelicateCoroutinesApi
    fun newSearch(query: String) {
        if (query.isNotEmpty()) {
            results.value = mutableListOf()
            loading.value = true
            val db = Database()
            GlobalScope.launch(Dispatchers.IO) {
                val result = searcher.search(query)

                results.value = result
                loading.value = false
            }
        }
    }
    fun updateFilterConfiguration() {
        Log.e("ViewModel", ingredient.value.toString())
        val newFc: FilterConfiguration = FilterConfiguration(
            maxKcal.value,
            minKcal.value,
            maxFat.value,
            minFat.value,
            maxSugar.value,
            minSugar.value,
            ingredient.value,
            alcoholFree.value,
            celeryFree.value,
            crustaceanFree.value,
            dairyFree.value,
            eggFree.value,
            fishFree.value,
            glutenFree.value,
            keto.value,
            kosher.value,
            lupineFree.value,
            mustardFree.value,
            paleo.value,
            peanutFree.value,
            porkFree.value,
            redMeatFree.value,
            sesameFree.value,
            soyFree.value,
            treeNutFree.value,
            vegan.value,
            pescatarian.value,
            vegetarian.value,
            wheatFree.value
        )
        Log.e("ViewModel", newFc.ingredient.toString())
        searcher.setFilterConfiguration(newFc)
    }

    fun getMealIngredients(meal: Meal): List<String> {
        val viewer = MealViewer(meal)
        return viewer.breakdown()
    }

    fun searchSuggest(query: String) {
        GlobalScope.launch(Dispatchers.IO) {
            suggestions.value = searcher.getAutocompSuggestions(query)
        }
    }
}
