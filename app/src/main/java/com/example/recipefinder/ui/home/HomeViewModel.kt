package com.example.recipefinder.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipefinder.adaptor.DishRepository
import com.example.recipefinder.roomdb.Dish
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: DishRepository) : ViewModel() {

    private val _dishesFetchCompleted = MutableLiveData<Boolean>()
    val dishesFetchCompleted: LiveData<Boolean> get() = _dishesFetchCompleted
    private val _searchResults = MutableLiveData<List<Dish>>()
    val searchResults: LiveData<List<Dish>> get() = _searchResults

    fun fetchAndSaveDishes(cuisines: List<String>) {
        viewModelScope.launch {
            repository.fetchDishes(cuisines)
            _dishesFetchCompleted.postValue(true)
        }
    }

    fun fetchAndSaveIngredientsAndSteps() {
        viewModelScope.launch {
            repository.fetchAndSaveIngredientsAndSteps()
        }
    }

    suspend fun getDishCount(): Int {
        return repository.getDishCount()
    }

    suspend fun getAllDishes(): List<Dish> {
        return repository.getAllDishes()
    }

    fun searchDishesAndIngredients(query: String) {
        viewModelScope.launch {
            val dishes = repository.searchDishesAndIngredients(query)
            _searchResults.value = dishes
        }
    }

}
