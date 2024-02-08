package database

/**
 * Database Activity class, which holds all the implementation of IDatabase, and can be called from
 * anywhere
 *
 * Author: Alexander Fall
 */

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.level33_health_app.R
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import filter_config.FilterConfiguration
import food.AFood
import food.Food
import food.Meal
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import user.User
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.IllegalStateException
import java.net.HttpURLConnection
import java.net.URL

class Database : AppCompatActivity(), IDatabase<DBCollection> {

    // Variable holding the database reference
    private lateinit var db: FirebaseFirestore

    // HashMap containing the collection each DBCollection class belongs to
    private val tableMap: HashMap<String, CollectionReference>
        get() {
            return hashMapOf(
                "User" to getDatabase().collection("users")
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)
    }

    // Gives the database, and initializes it if it hasn't been already
    private fun getDatabase(): FirebaseFirestore {
        if (!this::db.isInitialized) {
            db = Firebase.firestore
            return db
        }
        return db
    }

    // Adds a single document to the database
    // Inputs: DBCollection object
    override fun add(e: DBCollection) {
        val table = tableMap[e.javaClass.simpleName]
        if (table != null) {
            // Generates id for new user
            var ref = table.document()
            // Gives the ID to store if the object is a user
            if (e is User) {
                if (e.id == "") {
                    e.id = ref.id
                } else {
                    ref = table.document(e.id)
                }
            }
            // Transaction to add data
            getDatabase().runTransaction {
                ref.set(e)
            }
        }
    }

    // Updates a single document in the database
    // Inputs: DBCollection object
    override fun update(e: DBCollection) {
        val table = tableMap[e.javaClass.simpleName]
        if (table != null) {
            // Gets the document id from the object
            val ref = table.document(e.id)
            // Transaction to update document
            getDatabase().runTransaction {
                ref.set(e)
            }
        }
    }

    // Gets a single document from the database using an already known ID in a specific collection,
    // and returns in appropriate class
    // Coroutine, must be run from a suspend function or GlobalScope.launch
    // Inputs: Class reference to be searched, ID to be searched for
    // Returns: DBCollection object, or null if document not found
    override suspend fun getById(c: Class<DBCollection>, id: String): DBCollection? {
        val table = tableMap[c.simpleName]
        // Transaction to get document
        val res = getDatabase().runTransaction {
            table?.document(id)?.get()
        }.await() // Waits for transaction to be completed before continuing
        // If there is a result, tries to convert it to object of class c
        return try {
            println(res.result)
            res.result?.toObject(c)
        } catch (ex: IllegalStateException) {
            println("$ex, data not yet collected")
            getById(c, id)
        }
    }

    // Gets all documents from the database in a specific collection, and returns in
    // appropriate class
    // Coroutine, must be run from a suspend function or GlobalScope.launch
    // Inputs: Class reference to be searched
    // Returns: List of DBCollection objects, or null if collection couldn't be found
    override suspend fun getAll(c: Class<DBCollection>): MutableList<DBCollection> {
        val table = tableMap[c.simpleName]
        val returnList: MutableList<DBCollection> = mutableListOf()
        // Transaction to get documents
        val res = getDatabase().runTransaction {
            table?.get()
        }.await() // Waits for transaction to be completed before continuing
        return try {
            val list = res.result
            if (list != null) {
                // Converts all documents to instances of class c and adds to the list
                for (i in list.documents) {
                    i.toObject(c)?.let { returnList.add(it) }
                }
            }
            returnList
        } catch (ex: IllegalStateException) {
            getAll(c)
        }
    }

    // Deletes a single document from the database in a specific collection,
    // given an already known ID
    // Inputs: DBCollection, ID to be deleted
    override fun delete(c: Class<DBCollection>, id: String) {
        val table = tableMap[c.simpleName]
        if (table != null) {
            // Transaction to delete
            getDatabase().runTransaction {
                table.document(id).delete()
            }
        }
    }

    // Adds a group of objects to the database as documents in their defined collection
    // Inputs: List of DBCollection
    override fun addAll(e: List<DBCollection>) {
        // Loops through all values and adds them individually
        for (value in e) {
            add(value)
        }
    }

    // Checks if an ID is being used in the database in a specific collection
    // Coroutine, must be run from a suspend function or GlobalScope.launch
    // Inputs: Class reference to be searched, ID to be searched for
    // Returns: True or False depending on whether it was found or not
    override suspend fun checkInTable(e: DBCollection, id: String): Boolean {
        val table = tableMap[e.javaClass.simpleName]
        // Transaction to get value from document
        val res = getDatabase().runTransaction {
            table?.document(id)?.get()
        }.await() // Waits for transaction to be completed before continuing
        return try {
            // If no result, returns false
            if (res.result == null) {
                false
                // Otherwise returns whether the document exists
            } else {
                res.result!!.exists()
            }
        } catch (ex: IllegalStateException) {
            checkInTable(e, id)
        }
    }

    // Gets all documents from the database in a specific collection, which has a value the same,
    // and returns in appropriate class
    // Coroutine, must be run from a suspend function or GlobalScope.launch
    // Inputs: Class reference to be searched, field to be searched, and value of said field
    // Returns: List of DBCollection objects, or null if collection couldn't be found
    override suspend fun getAllByField(
        c: Class<DBCollection>,
        field: String,
        value: Any
    ): MutableList<DBCollection> {
        val table = tableMap[c.simpleName]
        val returnList: MutableList<DBCollection> = mutableListOf()
        // Transaction to get documents
        val res = getDatabase().runTransaction {
            table?.whereEqualTo(field, value)?.get()
        }.await() // Waits for the transaction to be completed before continuing
        return try {
            val list = res.result
            if (list != null) {
                for (i in list.documents) {
                    // Converts all documents to instances of class c and adds to the list
                    i.toObject(c)?.let { returnList.add(it) }
                }
            }
            returnList
        } catch (ex: IllegalStateException) {
            getAllByField(c, field, value)
        }
    }

    // Gets the URL and text from it
    // Inputs: the url you're trying to reach
    // Returns: string the page has on it
    private fun getUrl(url: String): String {
        val i = URL(url)
        // Tries to get the text on the URL, prints the exception if the url is not able to be read
        return try {
            i.readText()
        } catch (ex: Exception) {
            println("Exception found: $ex")
            return "null"
        }
    }

    // Gets text from a Curl command sent to the API
    // Inputs: the uri of the measurement being used, and the id of the food being searched for
    // Returns: string the API returns
    private fun getCurl(uri: String, id: String): String {
        // The URL for the item specific part of the API, needs to be called through POST request
        val url =
            URL("https://api.edamam.com/api/food-database/v2/nutrients?app_id=d04e1dbf&app_key=ced940d197ad146cf15b3664b6629dfc")
        // Opens HTTP connection so Curl commands can be inputted
        val http = url.openConnection() as HttpURLConnection
        // Settings to allow the request through the API
        http.requestMethod = "POST"
        http.doOutput = true
        http.setRequestProperty("Content-Type", "application/json")
        http.setRequestProperty("Accept", "application/json")
        // The request itself, taking the uri and id
        val data =
            "{\n  \"ingredients\": [\n    {\n      \"quantity\": 1,\n      \"measureURI\": \"$uri\",\n      \"foodId\": \"$id\"\n    }\n  ]\n}"
        // Makes the request to the server in an output stream
        val stream = http.outputStream
        stream.write(data.toByteArray())
        val inputStream = http.inputStream
        // Reads the text coming in from the request, passes it to variable that will be returned
        val br = BufferedReader(InputStreamReader(inputStream))
        val str = br.readText()
        // Disconnects the HTTP connection so it doesn't stay open for longer than it needs to be
        http.disconnect()
        return str
    }

    // Gets all food from a search value from the API, returning as a list of Food objects
    // Inputs: Food string you wish to search for
    // Returns: List of Food objects
    override suspend fun getFood(food: String): MutableList<AFood> {
        var returnList = mutableListOf<AFood>() // List to be returned
        val urlFood = food.replace(" ", "%20") // Converts the url to readable by the API
        val url =
            "https://api.edamam.com/api/food-database/v2/parser?app_id=d04e1dbf&app_key=ced940d197ad146cf15b3664b6629dfc&ingr=$urlFood&nutrition-type=cooking"
        val result = getUrl(url)
        if (result == "null") return returnList
        // If the result is empty, which is easily shown by hints being empty, then an empty list is returned
        if (result.substring(
                result.indexOf("hints") + 8,
                result.indexOf("hints") + 10
            ) == "[]"
        ) return returnList
        // Gets the list to return from the JSON parser
        returnList = getFoodList(result)
        return returnList
    }

    // Gets the list of food for food searches
    // Inputs: The string you're searching through, which is in JSON format
    // Returns: List of Food objects
    private fun getFoodList(result: String): MutableList<AFood> {
        val returnList = mutableListOf<AFood>() // List to be returned
        val obj = JSONObject(result) // Gets the overall JSON formatting from the string
        val foodArray =
            obj.getJSONArray("hints")
        for (i in 0 until foodArray.length()) {
            val overall = foodArray.getJSONObject(i)
            val food = overall.getJSONObject("food")
            val id = food.getString("foodId")
            val name = food.getString("label")
            val category =
                food.getString("category")
            // If the item is a food, makes a food object
            if (category == "Generic foods") {
                val measureArray =
                    overall.getJSONArray("measures")
                for (j in 0 until measureArray.length()) {
                    val measure = measureArray.getJSONObject(j)
                    if (measure.getString("label") == "Gram") break // Stops the loop when it gets to the measurements (gram, onze, kilo)
                    val uri = measure.getString("uri")
                    val resultTwo = getCurl(uri, id)
                    val food2 = JSONObject(resultTwo)
                    val parsed =
                        food2.getJSONArray("ingredients").getJSONObject(0).getJSONArray("parsed")
                    val details = parsed.getJSONObject(0)
                    val itemMeasure = details.getString("measure")
                    val weight = food2.getDouble("totalWeight")
                    val calories = food2.getDouble("calories")
                    var fat = 0.0
                    try {
                        fat = food2.getJSONObject("totalNutrients").getJSONObject("FAT")
                            .getDouble("quantity")
                    } catch (ex: Exception) {
                        println("Rice has no fat lmao: Exception $ex")
                    }
                    val carbs = food2.getJSONObject("totalNutrients").getJSONObject("CHOCDF")
                        .getDouble("quantity")
                    val health = food2.getJSONArray("healthLabels")
                    val healthLabels = mutableListOf<String>()
                    for (k in 0 until health.length()) {
                        healthLabels.add(health.getString(k))
                    }
                    val foodObj =
                        Food(
                            name,
                            itemMeasure,
                            weight,
                            calories,
                            fat,
                            carbs,
                            id,
                            uri,
                            healthLabels
                        ) // The Food object formed from the JSON parsing
                    returnList.add(foodObj)
                    println("Test: $foodObj")
                }
                // If the object is a meal, creates a meal object
            } else if (category == "Generic meals") {
                val measureArray = overall.getJSONArray("measures")
                val measure = measureArray.getJSONObject(0)
                val uri = measure.getString("uri")
                val resultTwo = getCurl(uri, id)
                val meal = JSONObject(resultTwo)
                val parsed =
                    meal.getJSONArray("ingredients").getJSONObject(0).getJSONArray("parsed")
                val details = parsed.getJSONObject(0)
                val weight = meal.getDouble("totalWeight")
                val calories = meal.getDouble("calories")
                val fat = meal.getJSONObject("totalNutrients").getJSONObject("FAT")
                    .getDouble("quantity")
                val carbs = meal.getJSONObject("totalNutrients").getJSONObject("CHOCDF")
                    .getDouble("quantity")
                val health = meal.getJSONArray("healthLabels")
                val healthLabels = mutableListOf<String>()
                for (k in 0 until health.length()) {
                    healthLabels.add(health.getString(k))
                }
                val foodContents = details.getString("foodContentsLabel")
                // val ingredients = mutableListOf<Food>()
                val foodList = foodContents.split(";").distinct()
                println("Test: $foodList")
                // Loops through all the distinct foods that are in the meal, adding them to the food list for the meal
                /*for (j in foodList) {
                    println("Test: $j")
                    val urlFood = j.replace(" ", "%20")
                    val url =
                        "https://api.edamam.com/api/food-database/v2/parser?app_id=d04e1dbf&app_key=ced940d197ad146cf15b3664b6629dfc&ingr=$urlFood&nutrition-type=cooking"
                    val urlRes = getUrl(url)
                    if (urlRes == "null") continue
                    val objc = JSONObject(urlRes)
                    val foodId = objc.getJSONArray("hints").getJSONObject(0).getJSONObject("food")
                        .getString("foodId")
                    val foodName = objc.getJSONArray("hints").getJSONObject(0).getJSONObject("food")
                        .getString("label")
                    val foodMeasure =
                        objc.getJSONArray("hints").getJSONObject(0).getJSONArray("measures")
                            .getJSONObject(0).getString("uri")
                    ingredients.add(getSingleFood(foodId, foodMeasure, foodName))
                }*/
                val mealObj =
                    Meal(
                        name,
                        weight,
                        calories,
                        fat,
                        carbs,
                        id,
                        uri,
                        healthLabels,
                        foodList
                    ) // The meal object formed from the JSON parsing
                returnList.add(mealObj)
            }
        }
        return returnList // The list of combined meals and foods
    }

    // Gets all food from a search value from the API and filters them, returning as a list of Food objects
    // Inputs: Food string you wish to search for, and the filters being used
    // Returns: List of filtered Food objects
    override suspend fun getFood(food: String, fc: FilterConfiguration): MutableList<AFood> {
        var returnList = mutableListOf<AFood>() // List to be returned
        val urlFood = food.replace(" ", "%20") // Converts the url to readable by the API
        var url =
            "https://api.edamam.com/api/food-database/v2/parser?app_id=d04e1dbf&app_key=ced940d197ad146cf15b3664b6629dfc&ingr=$urlFood&nutrition-type=cooking"
        // Filters the calorie values if a min and/or max has been defined
        if (fc.minKcal != Int.MIN_VALUE) {
            url += "&calories=${fc.minKcal}"
            url += if (fc.maxKcal != Int.MAX_VALUE) {
                "${fc.maxKcal}"
            } else "%2B"
        } else if (fc.maxKcal != Int.MAX_VALUE) url += "&calories=${fc.maxKcal}"
        // Filters the fat values if a min and/or max has been defined
        if (fc.minFat != Int.MIN_VALUE) {
            url += "&nutrients%5BFAT%5D=${fc.minFat}"
            url += if (fc.maxFat != Int.MAX_VALUE) {
                "${fc.maxFat}"
            } else "%2B"
        } else if (fc.maxFat != Int.MAX_VALUE) url += "&nutrients%5BFAT%5D=${fc.maxFat}"
        // Filters the sugar values if a min and/or max has been defined
        if (fc.minSugar != Int.MIN_VALUE) {
            url += "&nutrients%5BSUGAR%5D=${fc.minSugar}"
            url += if (fc.maxSugar != Int.MAX_VALUE) {
                "${fc.maxSugar}"
            } else "%2B"
        } else if (fc.maxSugar != Int.MAX_VALUE) url += "&nutrients%5BSUGAR%5D=${fc.maxSugar}"
        val result = getUrl(url)
        if (result.substring(
                result.indexOf("hints") + 8,
                result.indexOf("hints") + 10
            ) == "[]"
        ) return returnList
        returnList = getFoodList(result)
        // Filtering of the list
        if (fc.ingredient) returnList = returnList.filterIsInstance<Food>().toMutableList()
        else returnList = returnList.filterIsInstance<Meal>().toMutableList()
        if (fc.alcoholFree) returnList =
            returnList.filter { it.healthLabels.contains("ALCOHOL_FREE") } as MutableList<AFood>
        if (fc.celeryFree) returnList =
            returnList.filter { it.healthLabels.contains("CELERY_FREE") } as MutableList<AFood>
        if (fc.crustaceanFree) returnList =
            returnList.filter { it.healthLabels.contains("CRUSTACEAN_FREE") } as MutableList<AFood>
        if (fc.dairyFree) returnList =
            returnList.filter { it.healthLabels.contains("DAIRY_FREE") } as MutableList<AFood>
        if (fc.eggFree) returnList =
            returnList.filter { it.healthLabels.contains("EGG_FREE") } as MutableList<AFood>
        if (fc.fishFree) returnList =
            returnList.filter { it.healthLabels.contains("FISH_FREE") } as MutableList<AFood>
        if (fc.glutenFree) returnList =
            returnList.filter { it.healthLabels.contains("GLUTEN_FREE") } as MutableList<AFood>
        if (fc.keto) returnList =
            returnList.filter { it.healthLabels.contains("KETO") } as MutableList<AFood>
        if (fc.kosher) returnList =
            returnList.filter { it.healthLabels.contains("KOSHER") } as MutableList<AFood>
        if (fc.lupineFree) returnList =
            returnList.filter { it.healthLabels.contains("LUPINE_FREE") } as MutableList<AFood>
        if (fc.mustardFree) returnList =
            returnList.filter { it.healthLabels.contains("MUSTARD_FREE") } as MutableList<AFood>
        if (fc.paleo) returnList =
            returnList.filter { it.healthLabels.contains("PALEO") } as MutableList<AFood>
        if (fc.peanutFree) returnList =
            returnList.filter { it.healthLabels.contains("PEANUT_FREE") } as MutableList<AFood>
        if (fc.porkFree) returnList =
            returnList.filter { it.healthLabels.contains("PORK_FREE") } as MutableList<AFood>
        if (fc.redMeatFree) returnList =
            returnList.filter { it.healthLabels.contains("RED_MEAT_FREE") } as MutableList<AFood>
        if (fc.sesameFree) returnList =
            returnList.filter { it.healthLabels.contains("SESAME_FREE") } as MutableList<AFood>
        if (fc.soyFree) returnList =
            returnList.filter { it.healthLabels.contains("SOY_FREE") } as MutableList<AFood>
        if (fc.treeNutFree) returnList =
            returnList.filter { it.healthLabels.contains("TREE_NUT_FREE") } as MutableList<AFood>
        if (fc.vegan) returnList =
            returnList.filter { it.healthLabels.contains("VEGAN") } as MutableList<AFood>
        if (fc.pescatarian) returnList =
            returnList.filter { it.healthLabels.contains("PESCATARIAN") } as MutableList<AFood>
        if (fc.vegetarian) returnList =
            returnList.filter { it.healthLabels.contains("VEGETARIAN") } as MutableList<AFood>
        if (fc.wheatFree) returnList =
            returnList.filter { it.healthLabels.contains("WHEAT_FREE") } as MutableList<AFood>
        return returnList
    }

    // Gets a single food object, if you already know the id and measure uri
    // Inputs: ID of the food, and URI of the measure, according to the API
    // Outputs: A single Food object
    override suspend fun getSingleFood(id: String, uri: String, name: String): Food {
        val url = getCurl(uri, id)
        val food = JSONObject(url)
        val parsed =
            food.getJSONArray("ingredients").getJSONObject(0).getJSONArray("parsed")
        val details = parsed.getJSONObject(0)
        val itemMeasure = details.getString("measure")
        val weight = food.getDouble("totalWeight")
        val calories = food.getDouble("calories")
        val fat = food.getJSONObject("totalNutrients").getJSONObject("FAT")
            .getDouble("quantity")
        val carbs = food.getJSONObject("totalNutrients").getJSONObject("CHOCDF")
            .getDouble("quantity")
        val health = food.getJSONArray("healthLabels")
        val healthLabels = mutableListOf<String>()
        for (k in 0 until health.length()) {
            healthLabels.add(health.getString(k))
        }
        return Food(
            name,
            itemMeasure,
            weight,
            calories,
            fat,
            carbs,
            id,
            uri,
            healthLabels
        ) // The Food object, gotten from the JSON parsing
    }

    // Gets a list of suggestions from the API depending on an inputted string
    // Inputs: Partial or full string you wish to find items with it in
    // Outputs: A list of strings that are items in the API
    override suspend fun getFoodSuggestions(str: String): List<String> {
        val urlStr = str.replace(" ", "%20")
        val url =
            "https://api.edamam.com/auto-complete?app_id=d04e1dbf&app_key=ced940d197ad146cf15b3664b6629dfc&q=$urlStr"
        val result = getUrl(url)
        return result.substring(1, result.length - 1).split(",").toList()
    }

    override suspend fun getIdByEmail(email: String): String? {
        val users = getAllByField(User().javaClass, "email", email)
        return if (users.size > 0) {
            (users[0] as User).id
        } else {
            null
        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        val users = getAllByField(User().javaClass, "email", email)
        return if (users.size > 0) {
            (users[0] as User)
        } else {
            null
        }
    }

    override suspend fun checkIfEmailAlreadyExists(email: String): Boolean =
        getAllByField(User().javaClass, "email", email).size > 0

    /*override suspend fun getUserById(id: String): User {
        return getById(User().javaClass, id) as User
    }*/
}
