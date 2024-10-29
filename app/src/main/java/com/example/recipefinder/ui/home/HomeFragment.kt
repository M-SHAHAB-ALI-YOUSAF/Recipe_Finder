package com.example.recipefinder.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.recipefinder.R
import com.example.recipefinder.adaptor.DishRepository
import com.example.recipefinder.adaptor.GridAdapter
import com.example.recipefinder.databinding.FragmentHomeBinding
import com.example.recipefinder.roomdb.AppDatabase
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var gridAdapter: GridAdapter
    private var job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize database and ViewModel
        val dishDao = AppDatabase.getDatabase(requireContext()).dishDao()
        val ingredientDao = AppDatabase.getDatabase(requireContext()).ingredientDao()
        val cookingStepDao = AppDatabase.getDatabase(requireContext()).cookingStepDao()
        val savedDishDao = AppDatabase.getDatabase(requireContext()).savedDishDao()
        val shoppingListDao = AppDatabase.getDatabase(requireContext()).shoppingListDao()

        val repository = DishRepository(dishDao, ingredientDao, cookingStepDao, savedDishDao, shoppingListDao)
        homeViewModel = ViewModelProvider(this, HomeViewModelFactory(repository)).get(HomeViewModel::class.java)

        // Set up UI elements
        val drawerImage = requireActivity().findViewById<ImageView>(R.id.drawer_image)
        val fragmentNameTextView = requireActivity().findViewById<TextView>(R.id.fragment_name_text_view)
        drawerImage?.visibility = View.VISIBLE
        fragmentNameTextView?.visibility = View.VISIBLE

        setupImageSlider()

        val cuisines = listOf("Italian", "Mexican", "Indian")
        fetchDishesAndIngredients(cuisines)

        homeViewModel.dishesFetchCompleted.observe(viewLifecycleOwner) { isCompleted ->
            if (isCompleted) {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        homeViewModel.fetchAndSaveIngredientsAndSteps()
                    }
                    displayDishes()
                }
            }
        }

        return binding.root
    }

    private fun setupImageSlider() {
        val imageSlider = binding.imageSlider
        val imageList = arrayListOf(
            SlideModel(R.drawable.img_4),
            SlideModel(R.drawable.img_2),
            SlideModel(R.drawable.img_3),
            SlideModel(R.drawable.img_8),
            SlideModel(R.drawable.img_7)
        )
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
    }

    private fun fetchDishesAndIngredients(cuisines: List<String>) {
        coroutineScope.launch(Dispatchers.IO) {
            val dishCount = homeViewModel.getDishCount()
            if (dishCount == 0) {
                homeViewModel.fetchAndSaveDishes(cuisines)
            }
            withContext(Dispatchers.Main) {
                displayDishes()
            }
        }
    }

    private suspend fun displayDishes() {
        val dishes = homeViewModel.getAllDishes()
        if (!::gridAdapter.isInitialized) {
            gridAdapter = GridAdapter(requireContext(), dishes)
            binding.gridView.adapter = gridAdapter
        } else {
            binding.gridView.adapter = gridAdapter
            gridAdapter.updateDishes(dishes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
