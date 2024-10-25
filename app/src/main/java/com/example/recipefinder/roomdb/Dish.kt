package com.example.recipefinder.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dishes")
data class Dish(
    @PrimaryKey val id: Int,
    val title: String,
    val image: String,
    val cuisine: String
)
