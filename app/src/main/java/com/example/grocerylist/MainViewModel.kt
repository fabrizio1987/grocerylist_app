package com.example.grocerylist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = GroceryDatabase.getDatabase(application).groceryDao()
    private val listIdLiveData = MutableLiveData<Int>()

    val allItems = listIdLiveData.switchMap { id -> dao.getItemsByList(id) }

    fun init(id: Int) {
        if (listIdLiveData.value == null) listIdLiveData.value = id
    }

    fun addItem(name: String) = viewModelScope.launch {
        val id = listIdLiveData.value ?: return@launch
        dao.insert(GroceryItem(listId = id, name = name))
    }

    fun toggleBought(item: GroceryItem) = viewModelScope.launch {
        dao.update(item.copy(isBought = !item.isBought))
    }

    fun deleteItem(item: GroceryItem) = viewModelScope.launch {
        dao.delete(item)
    }
}
