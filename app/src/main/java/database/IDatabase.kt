package database

import filter_config.FilterConfiguration
import food.AFood
import food.Food
import user.User

/**
 * Database Interface, which holds a list of the functionality the database must follow
 *
 * Author: Alexander Fall
 */

interface IDatabase<E> {
    fun add(e: E)
    fun update(e: E)
    suspend fun getById(c: Class<E>, id: String): DBCollection?
    suspend fun getAllByField(c: Class<E>, field: String, value: Any): MutableList<E>
    suspend fun getAll(c: Class<E>): MutableList<E>
    fun delete(c: Class<E>, id: String)
    fun addAll(e: List<E>)
    suspend fun checkInTable(e: E, id: String): Boolean
    suspend fun getFood(food: String): MutableList<AFood>
    suspend fun getFood(food: String, fc: FilterConfiguration): MutableList<AFood>
    suspend fun getSingleFood(id: String, uri: String, name: String): Food
    suspend fun getFoodSuggestions(str: String): List<String>
    suspend fun getIdByEmail(email: String): String?
    suspend fun getUserByEmail(email: String): User?
    suspend fun checkIfEmailAlreadyExists(email: String): Boolean
//    suspend fun getUserById(id: String): User
}
