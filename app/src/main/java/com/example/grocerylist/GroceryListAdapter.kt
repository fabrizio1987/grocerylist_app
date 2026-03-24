package com.example.grocerylist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylist.databinding.ItemListBinding

class GroceryListAdapter(
    private val onOpen: (GroceryList) -> Unit,
    private val onDelete: (GroceryList) -> Unit,
    private val onDragStart: (RecyclerView.ViewHolder) -> Unit
) : RecyclerView.Adapter<GroceryListAdapter.ViewHolder>() {

    private val items = mutableListOf<GroceryList>()

    fun submitList(newList: List<GroceryList>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newList.size
            override fun areItemsTheSame(old: Int, new: Int) = items[old].id == newList[new].id
            override fun areContentsTheSame(old: Int, new: Int) = items[old] == newList[new]
        })
        items.clear()
        items.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    fun moveItem(from: Int, to: Int) {
        val item = items.removeAt(from)
        items.add(to, item)
        notifyItemMoved(from, to)
    }

    fun getCurrentList(): List<GroceryList> = items.toList()

    inner class ViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun bind(list: GroceryList) {
            binding.textListName.text = list.name
            binding.root.setOnClickListener { onOpen(list) }
            binding.buttonDeleteList.setOnClickListener { onDelete(list) }
            binding.dragHandle.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) onDragStart(this)
                false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size
}
