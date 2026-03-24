package com.example.grocerylist

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GroceryListDao {
    @Query("SELECT * FROM grocery_lists ORDER BY position ASC")
    fun getAllLists(): LiveData<List<GroceryList>>

    @Insert
    suspend fun insert(list: GroceryList): Long

    @Update
    suspend fun update(list: GroceryList)

    @Delete
    suspend fun delete(list: GroceryList)
}
