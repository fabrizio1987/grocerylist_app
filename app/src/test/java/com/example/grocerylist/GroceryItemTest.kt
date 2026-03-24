package com.example.grocerylist

import org.junit.Assert.*
import org.junit.Test

class GroceryItemTest {

    @Test
    fun `new item is not bought by default`() {
        val item = GroceryItem(name = "Milk")
        assertFalse(item.isBought)
    }

    @Test
    fun `toggling bought flips the flag`() {
        val item = GroceryItem(name = "Eggs", isBought = false)
        val toggled = item.copy(isBought = !item.isBought)
        assertTrue(toggled.isBought)
    }

    @Test
    fun `toggling bought twice returns to original state`() {
        val item = GroceryItem(name = "Bread", isBought = false)
        val result = item.copy(isBought = !item.isBought).copy(isBought = !item.copy(isBought = !item.isBought).isBought)
        assertEquals(item.isBought, result.isBought)
    }

    @Test
    fun `items with same id are equal`() {
        val a = GroceryItem(id = 1, name = "Apple")
        val b = GroceryItem(id = 1, name = "Apple")
        assertEquals(a, b)
    }

    @Test
    fun `items with different ids are not equal`() {
        val a = GroceryItem(id = 1, name = "Apple")
        val b = GroceryItem(id = 2, name = "Apple")
        assertNotEquals(a, b)
    }

    @Test
    fun `item name is preserved`() {
        val item = GroceryItem(name = "Orange Juice")
        assertEquals("Orange Juice", item.name)
    }
}
