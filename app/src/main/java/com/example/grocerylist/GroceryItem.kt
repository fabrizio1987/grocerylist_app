package com.example.grocerylist

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "grocery_items",
    foreignKeys = [ForeignKey(
        entity = GroceryList::class,
        parentColumns = ["id"],
        childColumns = ["listId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("listId")]
)
data class GroceryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val listId: Int = 0,
    val name: String,
    val isBought: Boolean = false
)
