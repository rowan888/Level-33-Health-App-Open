// Author - Lucas Toner

package process_layer.food_searcher
import food.AFood
import food.Meal

open class FoodViewer(val foodItem: AFood) {

    // reports to the counter the current food has been reported
    // also displays the relevant view
    public fun reportToCounter() {
    }

    // displays the food viewer dialog
    public fun display() {
    }

    // returns the calorie count of the food item
    public fun getCalorieCount(): Double {
        return foodItem.calories
    }
}

class MealViewer(foodItem: AFood) : FoodViewer(foodItem) {

    // A list of all the ingredients in the meal
    public fun breakdown(): List<String> {
        val meal: Meal = foodItem as Meal
        return meal.ingredients
    }
}
