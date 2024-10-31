package com.example.recipefinder.roomdb.mealplanner

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_plan")
data class MealPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val date: String,
    val breakfast: String,
    val midMorningSnack: String,
    val lunch: String,
    val afternoonSnack: String,
    val dinner: String,
    val breakfastDishId: Int,
    val midMorningDishId: Int,
    val lunchDishId: Int,
    val afternoonDishId: Int,
    val dinnerDishId: Int
)

