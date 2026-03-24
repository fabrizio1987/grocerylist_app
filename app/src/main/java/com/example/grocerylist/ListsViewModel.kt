package com.example.grocerylist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ListsViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = GroceryDatabase.getDatabase(application).groceryListDao()
    val allLists = dao.getAllLists()

    fun addList(name: String) = viewModelScope.launch {
        val position = allLists.value?.size ?: 0
        dao.insert(GroceryList(name = name, position = position))
    }

    fun deleteList(list: GroceryList) = viewModelScope.launch {
        dao.delete(list)
    }

    fun commitMove(lists: List<GroceryList>) = viewModelScope.launch {
        lists.forEachIndexed { index, list ->
            dao.update(list.copy(position = index))
        }
    }
}
