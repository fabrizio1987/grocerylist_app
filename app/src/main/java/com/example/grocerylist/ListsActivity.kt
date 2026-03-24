package com.example.grocerylist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylist.databinding.ActivityListsBinding

class ListsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListsBinding
    private val viewModel: ListsViewModel by viewModels()
    private lateinit var adapter: GroceryListAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private var isDragging = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.my_lists)

        adapter = GroceryListAdapter(
            onOpen = { list ->
                startActivity(Intent(this, MainActivity::class.java).apply {
                    putExtra(MainActivity.EXTRA_LIST_ID, list.id)
                    putExtra(MainActivity.EXTRA_LIST_NAME, list.name)
                })
            },
            onDelete = { list -> viewModel.deleteList(list) },
            onDragStart = { viewHolder -> itemTouchHelper.startDrag(viewHolder) }
        )

        binding.recyclerLists.layoutManager = LinearLayoutManager(this)
        binding.recyclerLists.adapter = adapter

        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                rv: RecyclerView,
                from: RecyclerView.ViewHolder,
                to: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = from.adapterPosition
                val toPos = to.adapterPosition
                if (fromPos == RecyclerView.NO_POSITION || toPos == RecyclerView.NO_POSITION) return false
                adapter.moveItem(fromPos, toPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                isDragging = actionState == ItemTouchHelper.ACTION_STATE_DRAG
            }

            override fun clearView(rv: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(rv, viewHolder)
                isDragging = false
                viewModel.commitMove(adapter.getCurrentList())
            }
        }
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerLists)

        viewModel.allLists.observe(this) { lists ->
            if (!isDragging) adapter.submitList(lists)
        }

        binding.buttonAddList.setOnClickListener { addList() }
        binding.editListName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) { addList(); true } else false
        }
    }

    private fun addList() {
        val name = binding.editListName.text.toString().trim()
        if (name.isNotEmpty()) {
            viewModel.addList(name)
            binding.editListName.text?.clear()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lists, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_theme) {
            toggleTheme()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggleTheme() {
        val newMode = if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }
        getSharedPreferences("prefs", MODE_PRIVATE).edit().putInt("night_mode", newMode).apply()
        AppCompatDelegate.setDefaultNightMode(newMode)
    }
}
