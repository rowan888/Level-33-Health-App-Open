// Author - Lucas Toner

package filter_config

data class FilterConfiguration(
    val maxKcal: Int = Int.MAX_VALUE,
    val minKcal: Int = Int.MIN_VALUE,
    val maxFat: Int = Int.MAX_VALUE,
    val minFat: Int = Int.MIN_VALUE,
    val maxSugar: Int = Int.MAX_VALUE,
    val minSugar: Int = Int.MIN_VALUE,
    // determines whether food is meal or ingredient
    val ingredient: Boolean = true,
    // allergy/dietary info
    val alcoholFree: Boolean = false,
    val celeryFree: Boolean = false,
    val crustaceanFree: Boolean = false,
    val dairyFree: Boolean = false,
    val eggFree: Boolean = false,
    val fishFree: Boolean = false,
    val glutenFree: Boolean = false,
    val keto: Boolean = false,
    val kosher: Boolean = false,
    val lupineFree: Boolean = false,
    val mustardFree: Boolean = false,
    val paleo: Boolean = false,
    val peanutFree: Boolean = false,
    val porkFree: Boolean = false,
    val redMeatFree: Boolean = false,
    val sesameFree: Boolean = false,
    val soyFree: Boolean = false,
    val treeNutFree: Boolean = false,
    val vegan: Boolean = false,
    val pescatarian: Boolean = false,
    val vegetarian: Boolean = false,
    val wheatFree: Boolean = false
)
