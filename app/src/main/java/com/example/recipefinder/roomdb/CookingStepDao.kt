package com.example.recipefinder.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CookingStepDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
     fun insertCookingSteps(steps: List<CookingStepEntity>)

    @Query("SELECT * FROM cooking_steps WHERE dishId = :dishId ORDER BY stepNumber ASC")
    suspend fun getCookingStepsForDish(dishId: Int): List<CookingStepEntity>
}
