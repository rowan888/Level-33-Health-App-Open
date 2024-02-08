package user_interface.FoodSearcherScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import kotlinx.coroutines.DelicateCoroutinesApi

import filter_config.FilterConfiguration
import food.AFood
import food.Food
import food.Meal
import user_interface.CaloriesScreen.dailyCalorieIntakedLogged
import user_interface.ui.theme.Shapes

var fc: FilterConfiguration = FilterConfiguration()
@DelicateCoroutinesApi
private val viewModel: FoodSearcherViewModel = FoodSearcherViewModel()

// Main Composable for FoodSearcherScreen
@ExperimentalComposeUiApi
@DelicateCoroutinesApi
@ExperimentalMaterialApi
@Composable
fun FoodSearcherScreen(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box {

            Column {

                Spacer(modifier = Modifier.height(20.dp))

                // Title
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Food Database",
                    fontSize = 20.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {

                    // ALl filter conditions
                }
                Row {
                    MealOrIngredientCheckboxes()
                    ExpandableConditions()
                }
                // Loading indicator
                if (viewModel.loading.value) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colors.primary)
                    }
                }

                // Display of results
                val results: MutableList<AFood> = viewModel.results.value as MutableList<AFood>
                LazyColumn {
                    itemsIndexed(items = results) { index, food ->
                        FoodItemCard(food = food, onClick = {})
                    }
                }
            }

            // Description of food - overlays over all over components
            FoodDescription()
        }
    }
}

// Displays and expandable card, containing a list of checkboxes
// for users to select filter conditions
@DelicateCoroutinesApi
@ExperimentalMaterialApi
@Composable
fun ExpandableConditions(modifier: Modifier = Modifier) {

    // keeps track of wether the card is expanded
    var expandedState by remember {
        mutableStateOf(false)
    }

    // rotation of arrow
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )

    // Card containing all components
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(durationMillis = 300)
            ),
        shape = Shapes.medium,
        border = BorderStroke(width = 2.dp, color = Color.Black),
        onClick = {
            expandedState = !expandedState
        }
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Apply Filters (Allergies and Nutrients)",
                    modifier = Modifier
                        .weight(6f)
                        .padding(start = 5.dp)
                )
                // Spinning Arrow
                IconButton(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium)
                        .weight(1f)
                        .rotate(rotationState),
                    onClick = { expandedState != expandedState }
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop Down Arrow"
                    )
                }
            }
            if (expandedState) {
                // List of filter conditions
                FilterConfig()
                Spacer(modifier = Modifier.padding(6.dp))
            }
        }
    }
}

// List of filter condition Checkboxes
@DelicateCoroutinesApi
@Composable
fun FilterConfig() {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

        IncludeCheckBox(text = "Alcohol free", state = viewModel.alcoholFree)
        IncludeCheckBox(text = "Celery free", state = viewModel.celeryFree)
        IncludeCheckBox(text = "Crustacean free", state = viewModel.crustaceanFree)
        IncludeCheckBox(text = "Dairy free", state = viewModel.dairyFree)
        IncludeCheckBox(text = "Egg free", state = viewModel.eggFree)
        IncludeCheckBox(text = "Fish free", state = viewModel.fishFree)
        IncludeCheckBox(text = "Gluten free", state = viewModel.glutenFree)
        IncludeCheckBox(text = "Keto", state = viewModel.keto)
        IncludeCheckBox(text = "Kosher", state = viewModel.kosher)
        IncludeCheckBox(text = "Lupine free", state = viewModel.lupineFree)
        IncludeCheckBox(text = "Mustard free", state = viewModel.mustardFree)
        IncludeCheckBox(text = "Paleo", state = viewModel.paleo)
        IncludeCheckBox(text = "Peanut free", state = viewModel.peanutFree)
        IncludeCheckBox(text = "Pork free", state = viewModel.porkFree)
        IncludeCheckBox(text = "Red Meat free", state = viewModel.redMeatFree)
        IncludeCheckBox(text = "Sesame free", state = viewModel.sesameFree)
        IncludeCheckBox(text = "Soy free", state = viewModel.soyFree)
        IncludeCheckBox(text = "Tree Nut free", state = viewModel.treeNutFree)
        IncludeCheckBox(text = "Vegan", state = viewModel.vegan)
        IncludeCheckBox(text = "Pescatarian", state = viewModel.pescatarian)
        IncludeCheckBox(text = "Vegetarian", state = viewModel.vegetarian)
        IncludeCheckBox(text = "Wheat free", state = viewModel.wheatFree)
    }
}

// Reusable Checkbox for filter conditions
@DelicateCoroutinesApi
@Composable
fun IncludeCheckBox(
    modifier: Modifier = Modifier,
    state: MutableState<Boolean>,
    text: String,
    onChange: (String) -> Unit = {}
) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = state.value,
            onCheckedChange = {
                state.value = it
                viewModel.updateFilterConfiguration() },
            modifier = Modifier.height(1.dp)
        )
        Text(text = text)
    }
}

// Displays two related checkboxes that determine
// whether results will be ingredients or meals
@DelicateCoroutinesApi
@Composable
fun MealOrIngredientCheckboxes() {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(6.dp)
        ) {
            Checkbox(
                checked = viewModel.ingredient.value,
                onCheckedChange = {
                    viewModel.ingredient.value = it
                    viewModel.updateFilterConfiguration() },
                modifier = Modifier.height(1.dp)
            )
            Text(text = "Ingredients")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(6.dp)
        ) {
            Checkbox(
                checked = !viewModel.ingredient.value,
                onCheckedChange = {
                    viewModel.ingredient.value = !it
                    viewModel.updateFilterConfiguration() },
                modifier = Modifier.height(1.dp)
            )
            Text(text = "Meals")
        }
    }
}

// Displays Search Bar, and search suggestions
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@DelicateCoroutinesApi
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {

        // Search Bar
        OutlinedTextField(
            value = text, onValueChange = {
                text = it
                onSearch(it)
                viewModel.searchSuggest(it)
            },
            maxLines = 1,
            shape = CircleShape,
            label = { Text("Search", color = MaterialTheme.colors.primary) },
            keyboardActions = KeyboardActions(onDone = {
                viewModel.newSearch(text)
                viewModel.suggestions.value = listOf()
                focusManager.clearFocus()
            }),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text

            ),
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        )

        // Suggestion Card - updated whenever the value in the search bar is changed
        Card {
            LazyColumn {
                itemsIndexed(items = viewModel.suggestions.value) { index, suggestion ->
                    // When card is clicked, disappear and search
                    Card(
                        onClick = {
                            text = suggestion.trim('"')
                            viewModel.newSearch(text)
                            viewModel.suggestions.value = listOf()
                            focusManager.clearFocus()
                    }
                    ) {
                        Text(suggestion.trim('"'))
                    }
                }
            }
        }
    }
}

// Displays an individual search result
@DelicateCoroutinesApi
@ExperimentalMaterialApi
@Composable
fun FoodItemCard(food: AFood, onClick: () -> Unit) {
    Card(
        shape = CircleShape,
        modifier = Modifier.padding(top = 2.dp),
        elevation = 3.dp,
        border = BorderStroke(width = 2.dp, color = Color.Black),
        onClick = { viewModel.selectedFood.value = food }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            // Food and Meals have different cards - food contains a measure
            if (food is Food) {
                Text(
                    food.name + " " + food.measure + " (" + food.calories.toString() + "kcal)",
                    modifier = Modifier
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .weight(4f)
                        .padding(bottom = 6.dp, top = 6.dp)
                )
            } else {
                Text(
                    food.name + " (" + food.calories.toString() + "kcal)",
                    modifier = Modifier
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .weight(4f)
                        .padding(bottom = 6.dp, top = 6.dp)
                )
            }

            var str = "Ingredient"
            if (food is Meal) {
                str = "Meal"
            }
            Text(
                str,
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

// Displays information about a particular food, with an option to add to calorie counter
@DelicateCoroutinesApi
@Composable
fun FoodDescription() {
    if (viewModel.selectedFood.value != null) {
        val food: AFood = viewModel.selectedFood.value!!

        Card(modifier = Modifier.fillMaxWidth(0.8f), elevation = 20.dp) {
            Column() {
                // Close button
                Button(onClick = { viewModel.selectedFood.value = null }, modifier = Modifier.align(Alignment.End)) {
                    Text("Close")
                }
                // Information
                Text(text = food.name, fontSize = 40.sp, color = MaterialTheme.colors.primary, textAlign = TextAlign.Center)
                if (food is Food) Text(text = food.measure, fontSize = 30.sp, color = MaterialTheme.colors.primary, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.padding(10.dp))
                Text(text = "Calories (kcal) - " + food.calories.toString(), textAlign = TextAlign.Center)
                Text(text = "Fat (g) - " + food.fat.toString(), textAlign = TextAlign.Center)
                Text(text = "Carbohydrates (g) - " + food.carbs.toString(), textAlign = TextAlign.Center)
                Button(onClick = { dailyCalorieIntakedLogged(food.calories.toString()) }) {
                    Text("Add to Calorie Counter")
                }
                // List of Ingredients, if the food is a meal
                if (food is Meal) {
                    val ingredients: List<String> = viewModel.getMealIngredients(food) as List<String>
                    LazyColumn {
                        itemsIndexed(items = ingredients) { index, ingredient ->
                            Text(ingredient)
                        }
                    }
                }
            }
        }
    }
}
