package com.example.recipefinder.roomdb.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_dishes")
data class SavedDishEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dishId: Long
)