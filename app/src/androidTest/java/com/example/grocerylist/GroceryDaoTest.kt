package com.example.grocerylist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class GroceryDaoTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private lateinit var db: GroceryDatabase
    private lateinit var dao: GroceryDao
    private var testListId = 1

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GroceryDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.groceryDao()
        val listDao = db.groceryListDao()
        runBlocking {
            testListId = listDao.insert(GroceryList(name = "Test")).toInt()
        }
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertItem_appearsInList() = runTest {
        dao.insert(GroceryItem(listId = testListId, name = "Milk"))
        val items = dao.getItemsByList(testListId).getOrAwait()
        assertEquals(1, items.size)
        assertEquals("Milk", items[0].name)
    }

    @Test
    fun deleteItem_removedFromList() = runTest {
        dao.insert(GroceryItem(listId = testListId, name = "Eggs"))
        val inserted = dao.getItemsByList(testListId).getOrAwait().first()
        dao.delete(inserted)
        val items = dao.getItemsByList(testListId).getOrAwait()
        assertTrue(items.isEmpty())
    }

    @Test
    fun updateItem_isBoughtChanges() = runTest {
        dao.insert(GroceryItem(listId = testListId, name = "Bread"))
        val inserted = dao.getItemsByList(testListId).getOrAwait().first()
        dao.update(inserted.copy(isBought = true))
        val updated = dao.getItemsByList(testListId).getOrAwait().first()
        assertTrue(updated.isBought)
    }

    @Test
    fun itemsOrdered_unboughtFirst() = runTest {
        dao.insert(GroceryItem(listId = testListId, name = "A"))
        dao.insert(GroceryItem(listId = testListId, name = "B"))
        val all = dao.getItemsByList(testListId).getOrAwait()
        dao.update(all[0].copy(isBought = true))
        val ordered = dao.getItemsByList(testListId).getOrAwait()
        assertFalse(ordered[0].isBought)
        assertTrue(ordered.last().isBought)
    }

    private fun <T> androidx.lifecycle.LiveData<T>.getOrAwait(): T {
        var value: T? = null
        val latch = CountDownLatch(1)
        val observer = Observer<T> {
            value = it
            latch.countDown()
        }
        observeForever(observer)
        latch.await(2, TimeUnit.SECONDS)
        removeObserver(observer)
        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}
