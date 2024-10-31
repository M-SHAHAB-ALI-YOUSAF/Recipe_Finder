package com.example.recipefinder.ui.mealplanner

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipefinder.roomdb.Dish
import com.example.recipefinder.adaptor.DishRepository
import kotlinx.coroutines.launch

class AddMealTopPanViewModel(private val repository: DishRepository) : ViewModel() {

    private val _dishes = MutableLiveData<List<Dish>>()
    val dishes: LiveData<List<Dish>> get() = _dishes

    fun fetchDishes() {
        viewModelScope.launch { _dishes.value = repository.getAllDishes() }
    }

    fun getDishIdByTitle(title: String): Int {
        return _dishes.value?.firstOrNull { it.title == title }?.id ?: -1 // Return -1 if not found
    }

    fun checkForDuplicateAndSaveMealPlan(
        context: Context, email: String, date: String, breakfast: String,
        midMorning: String, lunch: String, afternoonSnack: String, dinner: String,
        breakfastDishId: Int, midMorningDishId: Int, lunchDishId: Int,
        afternoonDishId: Int, dinnerDishId: Int
    ) {
        viewModelScope.launch {
            val exists = repository.doesMealPlanExist(email, date)
            if (exists) {
                Toast.makeText(context, "Record already exists for this date.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                repository.saveMealPlan(
                    email, date, breakfast, midMorning, lunch, afternoonSnack, dinner,
                    breakfastDishId, midMorningDishId, lunchDishId, afternoonDishId, dinnerDishId
                )
                Toast.makeText(context, "Data added successfully.", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
