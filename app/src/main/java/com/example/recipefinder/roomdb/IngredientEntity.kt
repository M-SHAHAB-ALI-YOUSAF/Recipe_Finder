package com.example.recipefinder.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dishId: Int,
    val name: String,
    val amount: Double,
    val unit: String
)
