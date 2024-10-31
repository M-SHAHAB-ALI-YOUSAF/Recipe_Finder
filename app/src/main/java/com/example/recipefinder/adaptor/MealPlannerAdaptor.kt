package com.example.recipefinder.adaptor

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipefinder.R
import com.example.recipefinder.roomdb.Dish

class MealPlannerAdaptor(
    private var dishes: List<Dish>,
    private val onDeleteClick: (Dish) -> Unit
) : RecyclerView.Adapter<MealPlannerAdaptor.DishViewHolder>() {

    private val mealTimes = listOf(
        "Breakfast",
        "Mid-Morning Snack",
        "Lunch",
        "Afternoon Snack",
        "Dinner"
    )

    class DishViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dishImage: de.hdodenhof.circleimageview.CircleImageView =
            view.findViewById(R.id.dish_image_meal_plan)
        val eatTime: TextView = view.findViewById(R.id.eatTime)
        val dishName: TextView = view.findViewById(R.id.dish_name_meal_plan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.meal_plan_list_items, parent, false)
        return DishViewHolder(view)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val dish = dishes[position]

        Glide.with(holder.itemView.context)
            .load(dish.image)
            .placeholder(R.drawable.loading)
            .error(R.drawable.error)
            .into(holder.dishImage)

        holder.dishName.text = dish.title

        holder.eatTime.text = mealTimes[position]
        holder.dishName.isSelected = true
        holder.eatTime.isSelected = true
    }

    override fun getItemCount(): Int = dishes.size


    @SuppressLint("NotifyDataSetChanged")
    fun updateDishes(newDishes: List<Dish>) {
        dishes = newDishes
        notifyDataSetChanged()
    }
}
