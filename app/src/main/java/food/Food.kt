package food

/**
 * Class defining a food object, inheriting all the values from AFood, and adding the measure of
 * the food item
 *
 * Author: Alexander Fall
 */

open class Food(
    name: String,
    measure: String,
    weight: Double,
    calories: Double,
    fat: Double,
    carbs: Double,
    id: String,
    measureUri: String,
    healthLabels: List<String>
) : AFood() {
    override var name: String = name
        get() = field
        set(value) {
            field = value
        }
    var measure: String = measure
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
        return "Food(name='$name, $measure', weight=$weight, calories=$calories, fat=$fat, carbs=$carbs, healthLabels=$healthLabels)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Food

        if (name != other.name) return false
        if (measure != other.measure) return false
        if (weight != other.weight) return false
        if (calories != other.calories) return false
        if (fat != other.fat) return false
        if (carbs != other.carbs) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + weight.hashCode()
        result = 31 * result + calories.hashCode()
        result = 31 * result + fat.hashCode()
        result = 31 * result + carbs.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}
