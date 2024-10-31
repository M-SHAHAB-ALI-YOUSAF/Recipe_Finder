package com.example.recipefinder.roomdb

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.recipefinder.roomdb.favorite.SavedDishDao
import com.example.recipefinder.roomdb.favorite.SavedDishEntity
import com.example.recipefinder.roomdb.mealplanner.MealPlanDao
import com.example.recipefinder.roomdb.mealplanner.MealPlanEntity
import com.example.recipefinder.roomdb.shopping.ShoppingListDao
import com.example.recipefinder.roomdb.shopping.ShoppingListItem

@Database(
    entities = [Dish::class, IngredientEntity::class, CookingStepEntity::class, SavedDishEntity::class, ShoppingListItem::class, MealPlanEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dishDao(): DishDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun cookingStepDao(): CookingStepDao
    abstract fun savedDishDao(): SavedDishDao
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun mealPLanDao(): MealPlanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Recipe_Finder"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
