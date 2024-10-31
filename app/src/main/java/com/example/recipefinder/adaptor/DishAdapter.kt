package com.example.recipefinder.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipefinder.R
import com.example.recipefinder.roomdb.Dish

class DishAdapter(private val onItemClick: (Dish) -> Unit) :
    ListAdapter<Dish, DishAdapter.DishViewHolder>(DishDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dish, parent, false)
        return DishViewHolder(view)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class DishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.dish_name)
        private val imageView: ImageView = itemView.findViewById(R.id.dish_image)

        fun bind(dish: Dish, onItemClick: (Dish) -> Unit) {
            nameTextView.text = dish.title
            Glide.with(itemView.context)
                .load(dish.image)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(imageView)


            itemView.setOnClickListener {
                onItemClick(dish)
            }
        }
    }

    class DishDiffCallback : DiffUtil.ItemCallback<Dish>() {
        override fun areItemsTheSame(oldItem: Dish, newItem: Dish): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Dish, newItem: Dish): Boolean {
            return oldItem == newItem
        }
    }
}

