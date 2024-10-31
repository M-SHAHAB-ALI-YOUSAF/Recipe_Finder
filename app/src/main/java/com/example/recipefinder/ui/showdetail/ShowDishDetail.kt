package com.example.recipefinder.ui.showdetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.recipefinder.R
import com.example.recipefinder.adaptor.DishRepository
import com.example.recipefinder.databinding.FragmentShowDishDetailBinding
import com.example.recipefinder.roomdb.AppDatabase
import com.example.recipefinder.roomdb.IngredientEntity
import com.example.recipefinder.roomdb.CookingStepEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ShowDishDetail : Fragment() {

    private var dishId: Long? = null
    private var dishName: String? = null
    private var dishImage: String? = null

    private var _binding: FragmentShowDishDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowDishDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        arguments?.let {
            dishId = it.getLong("dish_id")
            dishName = it.getString("dish_name")
            dishImage = it.getString("dish_image")
        }

        binding.foodName.text = dishName

        Glide.with(requireContext())
            .load(dishImage)
            .placeholder(R.drawable.loading)
            .error(R.drawable.error)
            .centerCrop()
            .into(binding.dishPic)

        val dishDao = AppDatabase.getDatabase(requireContext()).dishDao()
        val ingredientDao = AppDatabase.getDatabase(requireContext()).ingredientDao()
        val cookingStepDao = AppDatabase.getDatabase(requireContext()).cookingStepDao()
        val savedDishDao = AppDatabase.getDatabase(requireContext()).savedDishDao()
        val shoppingListDao = AppDatabase.getDatabase(requireContext()).shoppingListDao()
        val mealPlanDao = AppDatabase.getDatabase(requireContext()).mealPLanDao()


        if (dishId != null) {
            val viewModel =
                ShowDishDetailViewModel(
                    DishRepository(
                        dishDao,
                        ingredientDao,
                        cookingStepDao,
                        savedDishDao,
                        shoppingListDao,
                        mealPlanDao
                    )
                )

            viewLifecycleOwner.lifecycleScope.launch {
                val isSaved = withContext(Dispatchers.IO) {
                    viewModel.isDishSaved(dishId!!)
                }
                val isShopping = withContext(Dispatchers.IO) {
                    viewModel.isDishSavedinShopping(dishId!!)
                }
                if (isSaved) {
                    binding.addToFavorite.setImageResource(R.drawable.heart)
                }
                if (isShopping) {
                    binding.addToShop.setImageResource(R.drawable.shopping)
                    binding.addToShop.isClickable = false
                }

                binding.addToFavorite.setOnClickListener {
                    if (!isSaved) {
                        dishId?.let { id ->
                            viewModel.saveDishId(id)
                            binding.addToFavorite.setImageResource(R.drawable.heart)
                        }
                    }
                }

                binding.addToShop.setOnClickListener {
                    if (!isShopping) {
                        dishId?.let { id ->
                            viewLifecycleOwner.lifecycleScope.launch {
                                val ingredients = viewModel.getIngredients(id.toInt())
                                viewModel.saveIngredientsToShoppingList(ingredients, id.toInt())
                                binding.addToShop.setImageResource(R.drawable.shopping)
                                binding.addToShop.isClickable = false
                                Toast.makeText(
                                    requireContext(),
                                    "Ingredients added to shopping list!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Ingredients already in shopping list!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            }

            viewModel.fetchIngredients(dishId!!.toInt()) { ingredients ->
                displayIngredients(ingredients)
            }
            viewModel.fetchCookingSteps(dishId!!.toInt()) { steps ->
                displayCookingSteps(steps)
            }
        }


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val drawerImage = requireActivity().findViewById<ImageView>(R.id.drawer_image)
        val fragmentNameTextView =
            requireActivity().findViewById<TextView>(R.id.fragment_name_text_view)

        drawerImage.visibility = View.GONE
        fragmentNameTextView.visibility = View.GONE
    }

    private fun displayIngredients(ingredients: List<IngredientEntity>) {
        val ingredientList = ingredients.joinToString(separator = "\n") { it ->
            "${it.amount} ${it.unit} ${
                it.name.split(" ").joinToString(" ") { word ->
                    word.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    }
                }
            }"
        }
        binding.ingredientAmount.text = ingredientList

        val ingredientNames = ingredients.joinToString(separator = "\n") { it ->
            it.name.split(" ").joinToString(" ") { word ->
                word.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
            }
        }
        binding.IngredeintsItems.text = ingredientNames
    }

    private fun displayCookingSteps(steps: List<CookingStepEntity>) {
        val stepList = steps.joinToString(separator = "\n\n") { it ->
            "${it.stepNumber}. ${
                it.description.split(" ").joinToString(" ") { word ->
                    word.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    }
                }
            }"
        }
        binding.Steps.append(stepList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
