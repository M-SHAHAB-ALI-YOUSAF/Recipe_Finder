package com.example.recipefinder.ui.showdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipefinder.adaptor.DishRepository
import com.example.recipefinder.roomdb.CookingStepEntity
import com.example.recipefinder.roomdb.IngredientEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShowDishDetailViewModel(private val repository: DishRepository) : ViewModel() {

    fun fetchIngredients(dishId: Int, callback: (List<IngredientEntity>) -> Unit) {
        viewModelScope.launch {
            val ingredients = repository.getIngredientsForDish(dishId)
            callback(ingredients)
        }
    }

    fun fetchCookingSteps(dishId: Int, callback: (List<CookingStepEntity>) -> Unit) {
        viewModelScope.launch {
            val cookingSteps = repository.getCookingStepsForDish(dishId)
            callback(cookingSteps)
        }
     }

    fun saveDishId(dishId: Long) {
        viewModelScope.launch {
            repository.saveDishId(dishId)
        }
    }
    suspend fun isDishSaved(dishId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            repository.isDishSaved(dishId)
        }
    }

    suspend fun isDishSavedinShopping(dishId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            repository.isDishinShoppinglist(dishId)
        }
    }


    suspend fun getIngredients(dishId: Int): List<IngredientEntity> {
        return repository.getIngredientsForDish(dishId)
    }

    fun saveIngredientsToShoppingList(ingredients: List<IngredientEntity>, dishId: Int) {
        viewModelScope.launch {
            repository.saveIngredientsToShoppingList(ingredients, dishId)
        }
    }


}
