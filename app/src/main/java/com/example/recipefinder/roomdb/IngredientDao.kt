package com.example.recipefinder.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IngredientDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertIngredients(ingredients: List<IngredientEntity>)

    @Query("SELECT * FROM ingredients WHERE dishId = :dishId")
    suspend fun getIngredientsForDish(dishId: Int): List<IngredientEntity>

    @Query("SELECT * FROM ingredients WHERE name LIKE '%' || :query || '%'")
    suspend fun searchIngredients(query: String): List<IngredientEntity>
}
