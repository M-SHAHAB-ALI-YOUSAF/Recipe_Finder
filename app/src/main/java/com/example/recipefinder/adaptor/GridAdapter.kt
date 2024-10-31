package com.example.recipefinder.adaptor

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.recipefinder.R
import com.example.recipefinder.roomdb.Dish
import com.example.recipefinder.ui.showdetail.ShowDishDetail
import de.hdodenhof.circleimageview.CircleImageView


class GridAdapter(
    private val context: Context,
    private var dishes: List<Dish>
) : BaseAdapter() {

    fun updateDishes(newDishes: List<Dish>) {
        dishes = newDishes
        notifyDataSetChanged()
    }

    override fun getCount(): Int = dishes.size

    override fun getItem(position: Int): Dish = dishes[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_dish_grid, parent, false)

        val dish = getItem(position)

        val titleTextView: TextView = view.findViewById(R.id.dish_name)
        val imageView: CircleImageView = view.findViewById(R.id.dish_image)

        titleTextView.text = dish.title
        titleTextView.isSelected = true

        Glide.with(context)
            .load(dish.image)
            .placeholder(R.drawable.loading)
            .error(R.drawable.error)
            .into(imageView)

        view.setOnClickListener {
            val bundle = Bundle().apply {
                putLong("dish_id", dish.id.toLong())
                putString("dish_name", dish.title)
                putString("dish_image", dish.image)
            }

            val navController = (context as AppCompatActivity).supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment_content_home_screen)
                ?.findNavController()

            navController?.navigate(R.id.showDishDetail, bundle)
        }


        return view
    }
}
