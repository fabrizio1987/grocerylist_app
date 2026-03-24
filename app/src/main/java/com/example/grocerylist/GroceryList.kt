package com.example.grocerylist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grocery_lists")
data class GroceryList(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val position: Int = 0
)
