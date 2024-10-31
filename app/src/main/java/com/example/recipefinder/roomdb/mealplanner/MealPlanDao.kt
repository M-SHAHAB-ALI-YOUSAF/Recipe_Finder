package com.example.recipefinder.roomdb.mealplanner

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MealPlanDao {
    @Insert
    suspend fun insertMealPlan(mealPlan: MealPlanEntity)


    @Query("DELETE FROM meal_plan WHERE id = :mealId")
    suspend fun deleteMealRecord(mealId: Long)

    @Query("SELECT * FROM meal_plan WHERE userEmail = :email AND date = :date LIMIT 1")
    suspend fun getMealPlanByEmailAndDate(email: String, date: String): MealPlanEntity?

}