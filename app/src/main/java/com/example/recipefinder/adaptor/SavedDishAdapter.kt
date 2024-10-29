package com.example.recipefinder.adaptor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.recipefinder.R
import com.bumptech.glide.Glide
import com.example.recipefinder.roomdb.AppDatabase
import com.example.recipefinder.roomdb.Dish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedDishAdapter(
    private val context: Context,
    private var dishes: List<Dish>,
    private val onListEmpty: (Boolean) -> Unit
) : RecyclerView.Adapter<SavedDishAdapter.DishViewHolder>() {

    class DishViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dishImage: ImageView = view.findViewById(R.id.dish_image_favorite)
        val dishName: TextView = view.findViewById(R.id.dish_name_favorite)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.favorite_list_items, parent, false)
        return DishViewHolder(view)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val dish = dishes[position]
        holder.dishName.text = dish.title
        holder.dishName.isSelected = true
        Glide.with(context)
            .load(dish.image)
            .placeholder(R.drawable.loading)
            .error(R.drawable.error)
            .into(holder.dishImage)

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putLong("dish_id", dish.id.toLong())
                putString("dish_name", dish.title)
                putString("dish_image", dish.image)
            }
            holder.itemView.findNavController().navigate(R.id.showDishDetail, bundle)
        }

        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(context).apply {
                setTitle("Delete Confirmation")
                setMessage("Are you sure you want to delete this dish?")
                setPositiveButton("Yes") { _, _ ->
                    deleteDish(dish)
                    val updatedDishes = dishes.toMutableList().apply { removeAt(position) }
                    updateDishes(updatedDishes)
                    onListEmpty(updatedDishes.isEmpty())
                }
                setNegativeButton("No", null)
                create().show()
            }
        }
    }

    override fun getItemCount(): Int = dishes.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateDishes(newDishes: List<Dish>) {
        dishes = newDishes
        notifyDataSetChanged()
        onListEmpty(dishes.isEmpty())
    }

    private fun deleteDish(dish: Dish) {
        CoroutineScope(Dispatchers.IO).launch {
            val savedDishDao = AppDatabase.getDatabase(context).savedDishDao()
            savedDishDao.deleteDish(dish.id.toLong())
        }
    }
}
