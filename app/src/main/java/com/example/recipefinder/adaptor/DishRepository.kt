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
import com.example.recipefinder.roomdb.shopping.ShoppingListDao
import com.example.recipefinder.roomdb.shopping.ShoppingListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DishRepository(
    private val dishDao: DishDao,
    private val ingredientDao: IngredientDao,
    private val cookingStepDao: CookingStepDao,
    private val savedDishDao: SavedDishDao,
    private val shoppingListDao: ShoppingListDao
) {
    private val apiKey = "ec5a10a226264cc2b0948396fa0add1b"

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
            val ingredientsExist = withContext(Dispatchers.IO) {
                ingredientDao.getIngredientsForDish(dishId).isNotEmpty()
            }
            val stepsExist = withContext(Dispatchers.IO) {
                cookingStepDao.getCookingStepsForDish(dishId).isNotEmpty()
            }

            if (!ingredientsExist || !stepsExist) {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.spoonacularService.getRecipeInformation(dishId, apiKey)
                }

                if (!ingredientsExist) {
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
                }

                if (!stepsExist) {
                    val steps = response.analyzedInstructions.flatMap { instruction ->
                        instruction.steps.map { step ->
                            CookingStepEntity(
                                dishId = response.id,
                                stepNumber = step.number,
                                description = step.step
                            )
                        }
                    }
                    withContext(Dispatchers.IO) {
                        cookingStepDao.insertCookingSteps(steps)
                    }
                }
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

    suspend fun isDishinShoppinglist(dishId: Long): Boolean {
        return shoppingListDao.isDishSaved(dishId) > 0
    }


    suspend fun saveIngredientsToShoppingList(ingredients: List<IngredientEntity>, dishId: Int) {
        val shoppingListItems = ingredients.map { ingredient ->
            ShoppingListItem(
                dishId = dishId,
                ingredientName = ingredient.name
            )
        }
        withContext(Dispatchers.IO) {
            shoppingListDao.insertShoppingListItems(shoppingListItems)
        }
    }


}
