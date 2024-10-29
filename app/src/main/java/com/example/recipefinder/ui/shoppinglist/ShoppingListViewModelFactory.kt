package com.example.recipefinder.ui.shoppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipefinder.roomdb.shopping.ShoppingListDao

class ShoppingListViewModelFactory(private val shoppingListDao: ShoppingListDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingListViewModel::class.java)) {
            return ShoppingListViewModel(shoppingListDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
