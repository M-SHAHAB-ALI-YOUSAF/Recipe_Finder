package com.example.recipefinder.ui.mealplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipefinder.R
import com.example.recipefinder.adaptor.DateAdapter
import com.example.recipefinder.adaptor.DishRepository
import com.example.recipefinder.adaptor.OnDateSelectedListener
import com.example.recipefinder.adaptor.MealPlannerAdaptor
import com.example.recipefinder.databinding.FragmentMealPlannerBinding
import com.example.recipefinder.dataclass.FirebaseAuthUtil
import com.example.recipefinder.roomdb.AppDatabase
import java.text.SimpleDateFormat
import java.util.*

class MealPlanner : Fragment(), OnDateSelectedListener {

    private lateinit var viewModel: MealPlannerViewModel
    private var _binding: FragmentMealPlannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var datesAdapter: DateAdapter
    private lateinit var mealPlannerAdaptor: MealPlannerAdaptor
    private var selectedDate: String? = null
    private lateinit var repository: DishRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealPlannerBinding.inflate(inflater, container, false)

        val dishDao = AppDatabase.getDatabase(requireContext()).dishDao()
        val ingredientDao = AppDatabase.getDatabase(requireContext()).ingredientDao()
        val cookingStepDao = AppDatabase.getDatabase(requireContext()).cookingStepDao()
        val savedDishDao = AppDatabase.getDatabase(requireContext()).savedDishDao()
        val shoppingListDao = AppDatabase.getDatabase(requireContext()).shoppingListDao()
        val mealPlanDao = AppDatabase.getDatabase(requireContext()).mealPLanDao()
        repository =
            DishRepository(
                dishDao,
                ingredientDao,
                cookingStepDao,
                savedDishDao,
                shoppingListDao,
                mealPlanDao
            )

        viewModel = ViewModelProvider(this, MealPlannerViewModelFactory(repository)).get(
            MealPlannerViewModel::class.java
        )

        setupUI()
        setupObservers()

        val drawerImage = requireActivity().findViewById<ImageView>(R.id.drawer_image)
        drawerImage?.visibility = View.VISIBLE

        binding.addToPlan.setOnClickListener {
            findNavController().navigate(R.id.action_nav_meal_planner_to_addMealTopPan)
        }

        return binding.root
    }

    private fun setupUI() {
        mealPlannerAdaptor = MealPlannerAdaptor(emptyList()) { dish ->
        }

        binding.mealplannerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.mealplannerRecyclerView.adapter = mealPlannerAdaptor

        datesAdapter = DateAdapter(getNextSevenDays(), this)
        binding.datesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.datesRecyclerView.adapter = datesAdapter

        selectedDate = getNextSevenDays()[0]

        fetchMealPlanDetails()
    }


    override fun onDateSelected(selectedDate: String) {
        this.selectedDate = selectedDate
        fetchMealPlanDetails()
    }

    private fun setupObservers() {
        viewModel.dishes.observe(viewLifecycleOwner) { dishes ->
            mealPlannerAdaptor.updateDishes(dishes)
            if (dishes.isEmpty()) {
                binding.noMealPLan.visibility = View.VISIBLE
                binding.mealplannerRecyclerView.visibility = View.GONE
            } else {
                binding.noMealPLan.visibility = View.GONE
                binding.mealplannerRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun fetchMealPlanDetails() {
        val formattedDate = convertDateFormat(selectedDate)
        val userEmail = FirebaseAuthUtil.getCurrentUserEmail()
        if (userEmail.isNotEmpty() && formattedDate.isNotEmpty()) {
            viewModel.getDishesForUserAndDate(userEmail, formattedDate)
        } else {
            Toast.makeText(requireContext(), "Error fetching meal plan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertDateFormat(date: String?): String {
        val inputFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        return try {
            val parsedDate = inputFormat.parse(date.toString())
            val calendar = Calendar.getInstance()
            calendar.time = parsedDate!!
            calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
            outputFormat.format(calendar.time)

        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun getNextSevenDays(): List<String> {
        val dateList = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

        for (i in 0 until 7) {
            dateList.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return dateList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
