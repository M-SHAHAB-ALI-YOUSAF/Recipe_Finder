package com.example.recipefinder.adaptor


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipefinder.databinding.ShoppingListBinding
import com.example.recipefinder.roomdb.shopping.ShoppingListItem

class ShoppingListAdapter(
    private var items: List<ShoppingListItem>,
    private val onItemCheckedChange: (ShoppingListItem, Boolean) -> Unit
) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    inner class ShoppingListViewHolder(private val binding: ShoppingListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShoppingListItem) {
            binding.ingredientCheckbox.isChecked = item.isChecked
            binding.ingredientName.text = item.ingredientName

            binding.ingredientName.paint.isStrikeThruText = item.isChecked

            binding.ingredientCheckbox.setOnCheckedChangeListener { _, isChecked ->
                onItemCheckedChange(item, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val binding = ShoppingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<ShoppingListItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
