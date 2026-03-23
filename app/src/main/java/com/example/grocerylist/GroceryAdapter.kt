package com.example.grocerylist

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylist.databinding.ItemGroceryBinding

class GroceryAdapter(
    private val onToggle: (GroceryItem) -> Unit,
    private val onDelete: (GroceryItem) -> Unit
) : ListAdapter<GroceryItem, GroceryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemGroceryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GroceryItem) {
            binding.checkBox.setOnCheckedChangeListener(null)
            binding.checkBox.isChecked = item.isBought

            if (item.isBought) {
                binding.textItem.paintFlags = binding.textItem.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.textItem.alpha = 0.5f
            } else {
                binding.textItem.paintFlags = binding.textItem.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.textItem.alpha = 1.0f
            }
            binding.textItem.text = item.name

            binding.checkBox.setOnCheckedChangeListener { _, _ -> onToggle(item) }
            binding.buttonDelete.setOnClickListener { onDelete(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGroceryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<GroceryItem>() {
        override fun areItemsTheSame(oldItem: GroceryItem, newItem: GroceryItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: GroceryItem, newItem: GroceryItem) = oldItem == newItem
    }
}
