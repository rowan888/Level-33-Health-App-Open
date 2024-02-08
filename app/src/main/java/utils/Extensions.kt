package utils

fun String.isNumber(): Boolean {
    return try {
        toInt()
        true
    } catch (ex: NumberFormatException) {
        false
    }
}