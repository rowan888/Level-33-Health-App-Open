package food

/**
 * Class defining a food the user has eaten, adding the extra parameter of timeEaten
 *
 * Author: Alexander Fall
 */

import com.google.firebase.Timestamp

class UserFood(
    name: String,
    id: String,
    measureUri: String,
    timeEaten: Timestamp
) : Food(name, "", 0.0, 0.0, 0.0, 0.0, id, measureUri, listOf()) {
    var timeEaten: Timestamp = timeEaten
        get() = field
        set(value) {
            field = value
        }
    // Blank constructor for pulling from Firebase
    constructor() : this("", "", "", Timestamp.now())
}
