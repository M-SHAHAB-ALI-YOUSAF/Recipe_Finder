package com.example.recipefinder.ui.mealplanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipefinder.adaptor.DishRepository
import com.example.recipefinder.roomdb.Dish
import kotlinx.coroutines.launch

class MealPlannerViewModel(private val repository: DishRepository) : ViewModel() {

    private val _dishes = MutableLiveData<List<Dish>>()
    val dishes: LiveData<List<Dish>> get() = _dishes

    fun getDishesForUserAndDate(email: String, date: String) {
        viewModelScope.launch {
            val dishIds = repository.getMealPlanDishIds(email, date)
            val uniqueDishes = repository.getDishesByIds(dishIds.distinct())

            val dishDetails = dishIds.mapNotNull { id -> uniqueDishes.find { it.id == id.toInt() } }

            _dishes.value = dishDetails
        }
    }

}
