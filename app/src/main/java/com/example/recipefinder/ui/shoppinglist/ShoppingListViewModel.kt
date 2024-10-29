package com.example.recipefinder.ui.shoppinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipefinder.roomdb.shopping.ShoppingListDao
import com.example.recipefinder.roomdb.shopping.ShoppingListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShoppingListViewModel(private val shoppingListDao: ShoppingListDao) : ViewModel() {

    private val _shoppingListItems = MutableLiveData<List<ShoppingListItem>>()
    val shoppingListItems: LiveData<List<ShoppingListItem>> = _shoppingListItems

    init {
        fetchShoppingListItems()
    }

    private fun fetchShoppingListItems() {
        viewModelScope.launch {
            val items = withContext(Dispatchers.IO) {
                shoppingListDao.getAllShoppingListItems()
            }
            _shoppingListItems.value = items
        }
    }

    fun updateItem(item: ShoppingListItem) {
        viewModelScope.launch {
            shoppingListDao.updateShoppingListItem(item)
            fetchShoppingListItems()
        }
    }


    fun clearAllShoppingListItems() {
        viewModelScope.launch {
            shoppingListDao.clearAllShoppingListItems()
            fetchShoppingListItems()
        }
    }
}
