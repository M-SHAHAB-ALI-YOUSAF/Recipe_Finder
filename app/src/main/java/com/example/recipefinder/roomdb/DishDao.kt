package com.example.recipefinder.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DishDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDishes(dishes: List<Dish>)

    @Query("SELECT id FROM dishes")
    suspend fun getAllDishIds(): List<Int>

    @Query("SELECT COUNT(*) FROM dishes")
    suspend fun getDishCount(): Int

    @Query("SELECT * FROM dishes")
    suspend fun getAllDishes(): List<Dish>
}
