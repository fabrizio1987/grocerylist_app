package com.example.grocerylist

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GroceryDao {
    @Query("SELECT * FROM grocery_items ORDER BY isBought ASC, id DESC")
    fun getAllItems(): LiveData<List<GroceryItem>>

    @Insert
    suspend fun insert(item: GroceryItem)

    @Update
    suspend fun update(item: GroceryItem)

    @Delete
    suspend fun delete(item: GroceryItem)
}
