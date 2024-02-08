package user_interface

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.reflect.KFunction1

open class TextFieldState(
    private val validator: (String) -> Boolean,
    private val errorMessage: KFunction1<Any, String>
) {

    var text by mutableStateOf("")
    var error by mutableStateOf<String?>(null)

    fun validate() {
        error = if (isValid()) {
            null
        } else {
            errorMessage(text)
        }
    }

    fun isValid() = validator(text)
}
