package com.example.recipefinder.roomdb.shopping

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface ShoppingListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingListItems(items: List<ShoppingListItem>)

    @Query("DELETE FROM shopping_list WHERE ingredientName = :ingredientName")
    suspend fun deleteItemByIngredient(ingredientName: String)

    @Query("SELECT COUNT(*) FROM shopping_list WHERE dishId = :dishId")
    suspend fun isDishSaved(dishId: Long): Int

    @Query("SELECT * FROM shopping_list")
    suspend fun getAllShoppingListItems(): List<ShoppingListItem>

    @Update
    suspend fun updateShoppingListItem(item: ShoppingListItem)

    @Query("DELETE FROM shopping_list")
    suspend fun clearAllShoppingListItems()

}
