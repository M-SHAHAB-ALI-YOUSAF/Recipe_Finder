package com.example.recipefinder.adaptor

import android.util.Log
import com.example.recipefinder.retrofit.RetrofitClient
import com.example.recipefinder.roomdb.Dish
import com.example.recipefinder.roomdb.DishDao
import com.example.recipefinder.roomdb.IngredientDao
import com.example.recipefinder.roomdb.IngredientEntity
import com.example.recipefinder.roomdb.CookingStepDao
import com.example.recipefinder.roomdb.CookingStepEntity
import com.example.recipefinder.roomdb.favorite.SavedDishDao
import com.example.recipefinder.roomdb.favorite.SavedDishEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DishRepository(
    private val dishDao: DishDao,
    private val ingredientDao: IngredientDao,
    private val cookingStepDao: CookingStepDao,
    private val savedDishDao: SavedDishDao
) {
    private val apiKey = "3136a7cec7e940039b7a8f4253d6e406"

    suspend fun fetchDishes(cuisines: List<String>) {
        val dishes = mutableListOf<Dish>()

        for (cuisine in cuisines) {
            val response = RetrofitClient.spoonacularService.searchRecipes(cuisine, 5, apiKey)
            response.results.forEach { recipe ->
                dishes.add(Dish(recipe.id, recipe.title, recipe.image, cuisine))
            }
        }

        withContext(Dispatchers.IO) {
            dishDao.insertDishes(dishes)
        }
    }


    suspend fun fetchAndSaveIngredientsAndSteps() {
        val dishIds = withContext(Dispatchers.IO) {
            dishDao.getAllDishIds()
        }

        for (dishId in dishIds) {
            Log.d("DishRepository", "Fetched ingredients and steps for dishId: $dishId")
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.spoonacularService.getRecipeInformation(dishId, apiKey)
            }

            val ingredients = response.extendedIngredients.map { ingredient ->
                IngredientEntity(
                    dishId = response.id,
                    name = ingredient.name,
                    amount = ingredient.amount,
                    unit = ingredient.unit
                )
            }


            withContext(Dispatchers.IO) {
                ingredientDao.insertIngredients(ingredients)
            }

            val steps = response.analyzedInstructions.flatMap { instruction ->
                instruction.steps.map { step ->
                    CookingStepEntity(
                        dishId = response.id,
                        stepNumber = step.number,
                        description = step.step
                    )
                }
            }

            // Insert cooking steps in IO context
            withContext(Dispatchers.IO) {
                cookingStepDao.insertCookingSteps(steps)
            }
        }
    }


    suspend fun getDishCount(): Int {
        return dishDao.getDishCount()
    }

    suspend fun getAllDishes(): List<Dish> {
        return withContext(Dispatchers.IO) {
            dishDao.getAllDishes()
        }
    }


    suspend fun getIngredientsForDish(dishId: Int): List<IngredientEntity> {
        return withContext(Dispatchers.IO) {
            ingredientDao.getIngredientsForDish(dishId)
        }
    }


    suspend fun getCookingStepsForDish(dishId: Int): List<CookingStepEntity> {
        return withContext(Dispatchers.IO) {
            cookingStepDao.getCookingStepsForDish(dishId)
        }
    }

    suspend fun saveDishId(dishId: Long) {
        val savedDish = SavedDishEntity(dishId = dishId)
        withContext(Dispatchers.IO) {
            savedDishDao.insertSavedDish(savedDish)
        }
    }

    suspend fun isDishSaved(dishId: Long): Boolean {
        return savedDishDao.isDishSaved(dishId) > 0
    }

}
