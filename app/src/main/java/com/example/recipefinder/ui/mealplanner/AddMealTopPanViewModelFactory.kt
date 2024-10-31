package com.example.recipefinder.ui.mealplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipefinder.adaptor.DishRepository

class AddMealTopPanViewModelFactory(private val repository: DishRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddMealTopPanViewModel::class.java)) {
            return AddMealTopPanViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
