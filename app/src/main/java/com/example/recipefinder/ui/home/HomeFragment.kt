package com.example.recipefinder.ui.home

import android.os.Bundle
import android.util.Log
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
import com.example.recipefinder.adaptor.DishAdapter
import com.example.recipefinder.adaptor.DishRepository
import com.example.recipefinder.adaptor.GridAdapter
import com.example.recipefinder.databinding.FragmentHomeBinding
import com.example.recipefinder.roomdb.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        val dishDao = AppDatabase.getDatabase(requireContext()).dishDao()
        val ingredientDao = AppDatabase.getDatabase(requireContext()).ingredientDao()
        val cookingStepDao = AppDatabase.getDatabase(requireContext()).cookingStepDao()
        val savedDishDao = AppDatabase.getDatabase(requireContext()).savedDishDao()

        val repository = DishRepository(dishDao, ingredientDao, cookingStepDao, savedDishDao)
        homeViewModel = ViewModelProvider(this, HomeViewModelFactory(repository)).get(HomeViewModel::class.java)
        val drawerImage = requireActivity().findViewById<ImageView>(R.id.drawer_image)
        val fragmentNameTextView = requireActivity().findViewById<TextView>(R.id.fragment_name_text_view)

        drawerImage?.visibility = View.VISIBLE
        fragmentNameTextView?.visibility = View.VISIBLE
        setupImageSlider()
        val cuisines = listOf("Italian", "Mexican", "Indian")
        fetchDishesAndIngredients(cuisines)
        return binding.root
    }

    private fun setupImageSlider() {
        val imageSlider = binding.imageSlider
        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel(R.drawable.img_4))
        imageList.add(SlideModel(R.drawable.img_2))
        imageList.add(SlideModel(R.drawable.img_3))
        imageList.add(SlideModel(R.drawable.img_8))
        imageList.add(SlideModel(R.drawable.img_7))
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
    }

//    private fun fetchDishesAndIngredients(cuisines: List<String>) {
//        coroutineScope.launch {
//            val dishCount = withContext(Dispatchers.IO) {
//                homeViewModel.getDishCount()
//            }
//            if (dishCount == 0) {
//                withContext(Dispatchers.IO) {
//                    homeViewModel.fetchAndSaveDishes(cuisines)
//                }
//
//                withContext(Dispatchers.IO) {
//                    homeViewModel.fetchAndSaveIngredientsAndSteps()
//                }
//            }
//            displayDishes()
//        }
//    }

    private fun fetchDishesAndIngredients(cuisines: List<String>) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                homeViewModel.fetchAndSaveDishes(cuisines) // Fetch and save dishes first
                homeViewModel.fetchAndSaveIngredientsAndSteps() // Then fetch and save ingredients and steps
            }
            displayDishes()
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



