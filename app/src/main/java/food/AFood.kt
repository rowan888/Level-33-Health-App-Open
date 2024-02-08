package food

/**
 * Parent class of Meal and Food, holding basic information.
 * Contains food name, weight, calories, fat, carbs, id in the API, the URI of the measure used, and
 * health labels for filtering
 *
 * Author: Alexander Fall
 */

open class AFood {
    open var name: String = ""
        get() = field
        protected set(value) {
            field = value
        }
    open var weight: Double = 0.0
        get() = field
        protected set(value) {
            field = value
        }
    open var calories: Double = 0.0
        get() = field
        protected set(value) {
            field = value
        }
    open var fat: Double = 0.0
        get() = field
        protected set(value) {
            field = value
        }
    open var carbs: Double = 0.0
        get() = field
        protected set(value) {
            field = value
        }
    open var id: String = ""
        get() = field
        protected set(value) {
            field = value
        }
    open var measureUri: String = ""
        get() = field
        protected set(value) {
            field = value
        }
    open var healthLabels: List<String> = listOf()
        get() = field
        protected set(value) {
            field = value
        }
}
