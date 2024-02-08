package food

/**
 * Class defining a meal, inheriting from AFood and adding ingredients on top, which is a list of
 * Food objects
 *
 * Author: Alexander Fall
 */

class Meal(
    name: String,
    weight: Double,
    calories: Double,
    fat: Double,
    carbs: Double,
    id: String,
    measureUri: String,
    healthLabels: List<String>,
    ingredients: List<String>
) : AFood() {
    var ingredients = ingredients
        get() = field
    override var name: String = name
        get() = field
        set(value) {
            field = value
        }
    override var weight: Double = weight
        get() = field
        set(value) {
            field = value
        }
    override var calories: Double = calories
        get() = field
        set(value) {
            field = value
        }
    override var fat: Double = fat
        get() = field
        set(value) {
            field = value
        }
    override var carbs: Double = carbs
        get() = field
        set(value) {
            field = value
        }
    override var id: String = id
        get() = field
        set(value) {
            field = value
        }
    override var measureUri: String = measureUri
        get() = field
        set(value) {
            field = value
        }
    override var healthLabels: List<String> = healthLabels
        get() = field
        set(value) {
            field = value
        }
    override fun toString(): String {
        return "Meal(name='$name', weight=$weight, calories=$calories, fat=$fat, carbs=$carbs')"
    }
}
