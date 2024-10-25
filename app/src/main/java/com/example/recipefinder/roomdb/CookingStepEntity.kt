package com.example.recipefinder.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cooking_steps")
data class CookingStepEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dishId: Int,
    val stepNumber: Int,
    val description: String
)
