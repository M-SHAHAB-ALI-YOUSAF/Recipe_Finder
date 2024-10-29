package com.example.recipefinder.roomdb.favorite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SavedDishDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedDish(savedDish: SavedDishEntity)

    @Query("SELECT COUNT(*) FROM saved_dishes WHERE dishId = :dishId")
    suspend fun isDishSaved(dishId: Long): Int

    @Query("SELECT dishId FROM saved_dishes")
    suspend fun getSavedDishIds(): List<Long>

    @Query("DELETE FROM saved_dishes WHERE dishId = :dishId")
    suspend fun deleteDish(dishId: Long)
}
