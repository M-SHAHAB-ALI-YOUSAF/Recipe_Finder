package com.example.recipefinder.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.recipefinder.R
import com.example.recipefinder.adaptor.DishAdapter
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
    private lateinit var dishAdapter: DishAdapter
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
        val shoppingListDao = AppDatabase.getDatabase(requireContext()).shoppingListDao()
        val mealPlanDao = AppDatabase.getDatabase(requireContext()).mealPLanDao()

        val repository =
            DishRepository(
                dishDao,
                ingredientDao,
                cookingStepDao,
                savedDishDao,
                shoppingListDao,
                mealPlanDao
            )
        homeViewModel =
            ViewModelProvider(this, HomeViewModelFactory(repository)).get(HomeViewModel::class.java)


        val drawerImage = requireActivity().findViewById<ImageView>(R.id.drawer_image)
        val fragmentNameTextView =
            requireActivity().findViewById<TextView>(R.id.fragment_name_text_view)
        drawerImage?.visibility = View.VISIBLE
        fragmentNameTextView?.visibility = View.VISIBLE

        gridAdapter = GridAdapter(requireContext(), listOf())
        dishAdapter = DishAdapter { dish ->
            val bundle = Bundle().apply {
                putLong("dish_id", dish.id.toLong())
                putString("dish_name", dish.title)
                putString("dish_image", dish.image)
            }
            findNavController().navigate(R.id.showDishDetail, bundle)
        }
        binding.recyclerView.adapter = dishAdapter
        binding.gridView.adapter = gridAdapter

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerView.visibility = View.GONE
        binding.gridView.visibility = View.VISIBLE
        binding.noRecipesFound.visibility = View.GONE

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

        homeViewModel.searchResults.observe(viewLifecycleOwner) { dishes ->
            if (dishes.isNotEmpty()) {
                dishAdapter.submitList(dishes)
                binding.recyclerView.visibility = View.VISIBLE
                binding.gridView.visibility = View.GONE
                binding.noRecipesFound.visibility = View.GONE
            } else {
                binding.recyclerView.visibility = View.GONE
                binding.gridView.visibility = View.VISIBLE
                binding.noRecipesFound.visibility = View.VISIBLE
            }
        }


        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { query ->
                    if (query.isNotEmpty()) {
                        homeViewModel.searchDishesAndIngredients(query)
                    } else {
                        binding.recyclerView.visibility = View.GONE
                        binding.gridView.visibility = View.VISIBLE
                        binding.noRecipesFound.visibility = View.GONE
                        coroutineScope.launch(Dispatchers.IO) {
                            withContext(Dispatchers.Main) {
                                displayDishes()
                            }
                        }
                    }
                }
                return true
            }
        })

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        binding.recyclerView.visibility = View.GONE
        binding.gridView.visibility = View.VISIBLE
        binding.noRecipesFound.visibility = View.GONE
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
        gridAdapter.updateDishes(dishes)
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




