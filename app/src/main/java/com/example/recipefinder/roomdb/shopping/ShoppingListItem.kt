package com.example.recipefinder.roomdb.shopping

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list")
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dishId: Int,
    val ingredientName: String,
    val isChecked: Boolean = false
)
