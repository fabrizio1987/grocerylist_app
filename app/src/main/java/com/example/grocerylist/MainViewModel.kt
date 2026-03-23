package com.example.grocerylist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = GroceryDatabase.getDatabase(application).groceryDao()
    val allItems = dao.getAllItems()

    fun addItem(name: String) = viewModelScope.launch {
        dao.insert(GroceryItem(name = name))
    }

    fun toggleBought(item: GroceryItem) = viewModelScope.launch {
        dao.update(item.copy(isBought = !item.isBought))
    }

    fun deleteItem(item: GroceryItem) = viewModelScope.launch {
        dao.delete(item)
    }
}
