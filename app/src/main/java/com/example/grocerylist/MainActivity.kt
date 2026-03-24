package com.example.grocerylist

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerylist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = GroceryAdapter(
            onToggle = viewModel::toggleBought,
            onDelete = viewModel::deleteItem
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.allItems.observe(this) { adapter.submitList(it) }

        binding.buttonAdd.setOnClickListener { addItem() }
        binding.editTextItem.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) { addItem(); true } else false
        }
    }

    private fun addItem() {
        val name = binding.editTextItem.text.toString().trim()
        if (name.isNotEmpty()) {
            viewModel.addItem(name)
            binding.editTextItem.text?.clear()
        }
    }
}
