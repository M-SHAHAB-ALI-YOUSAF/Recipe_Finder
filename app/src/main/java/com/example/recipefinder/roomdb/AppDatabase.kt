package com.example.recipefinder.roomdb

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.recipefinder.roomdb.favorite.SavedDishDao
import com.example.recipefinder.roomdb.favorite.SavedDishEntity

@Database(entities = [Dish::class, IngredientEntity::class, CookingStepEntity::class, SavedDishEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dishDao(): DishDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun cookingStepDao(): CookingStepDao
    abstract fun savedDishDao(): SavedDishDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Recipe_Finder"
                ) .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
