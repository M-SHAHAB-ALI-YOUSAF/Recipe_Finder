package com.example.recipefinder.ui.mealplanner

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.recipefinder.R
import com.example.recipefinder.roomdb.Dish
import com.example.recipefinder.adaptor.DishRepository
import com.example.recipefinder.roomdb.AppDatabase
import com.example.recipefinder.databinding.FragmentAddMealTopPanBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class addMealTopPan : Fragment() {

    companion object {
        fun newInstance() = addMealTopPan()
    }

    private lateinit var viewModel: AddMealTopPanViewModel
    private var _binding: FragmentAddMealTopPanBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val dishDao = AppDatabase.getDatabase(requireContext()).dishDao()
        val ingredientDao = AppDatabase.getDatabase(requireContext()).ingredientDao()
        val cookingStepDao = AppDatabase.getDatabase(requireContext()).cookingStepDao()
        val savedDishDao = AppDatabase.getDatabase(requireContext()).savedDishDao()
        val shoppingListDao = AppDatabase.getDatabase(requireContext()).shoppingListDao()
        val mealPlanDao = AppDatabase.getDatabase(requireContext()).mealPLanDao()
        val repository = DishRepository(
            dishDao,
            ingredientDao,
            cookingStepDao,
            savedDishDao,
            shoppingListDao,
            mealPlanDao
        )
        val factory = AddMealTopPanViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AddMealTopPanViewModel::class.java)
        viewModel.fetchDishes()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMealTopPanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val drawerImage = requireActivity().findViewById<ImageView>(R.id.drawer_image)
        drawerImage.visibility = View.GONE
        viewModel.dishes.observe(viewLifecycleOwner) { dishes -> setupSpinners(dishes) }
        binding.SelectDate.setOnClickListener { showDatePickerDialog() }
        binding.addMealTopPlan.setOnClickListener {
            saveMealPlan()
        }
    }

    private fun setupSpinners(dishes: List<Dish>) {
        val dishTitles = dishes.map { it.title }
        setupSpinner(binding.breakfast, dishTitles, "Select Breakfast")
        setupSpinner(binding.MidMorning, dishTitles, "Select Mid-Morning Snack")
        setupSpinner(binding.lunch, dishTitles, "Select Lunch")
        setupSpinner(binding.AfternoonSnack, dishTitles, "Select Afternoon Snack")
        setupSpinner(binding.dinner, dishTitles, "Select Dinner")
    }

    private fun setupSpinner(spinner: Spinner, dishTitles: List<String>, hint: String) {
        val items = mutableListOf(hint).apply { addAll(dishTitles) }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)
        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) spinner.setSelection(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                spinner.setSelection(0)
            }
        })
    }

    private fun showDatePickerDialog() {
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val maxDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7) }
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "${dayOfMonth}/${month + 1}/$year"
                binding.SelectDate.text = selectedDate
            },
            tomorrow.get(Calendar.YEAR),
            tomorrow.get(Calendar.MONTH),
            tomorrow.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = tomorrow.timeInMillis
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis
        datePickerDialog.show()
    }

    private fun saveMealPlan() {
        val email = auth.currentUser?.email ?: return
        val selectedDate = binding.SelectDate.text.toString()
        val breakfast = binding.breakfast.selectedItem?.toString()
        val midMorning = binding.MidMorning.selectedItem?.toString()
        val lunch = binding.lunch.selectedItem?.toString()
        val afternoonSnack = binding.AfternoonSnack.selectedItem?.toString()
        val dinner = binding.dinner.selectedItem?.toString()

        if (selectedDate.isEmpty() || breakfast == "Select Breakfast" || midMorning == "Select Mid-Morning Snack" ||
            lunch == "Select Lunch" || afternoonSnack == "Select Afternoon Snack" || dinner == "Select Dinner"
        ) {
            Toast.makeText(
                requireContext(),
                "Please complete all fields before saving.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val breakfastDishId = viewModel.getDishIdByTitle(breakfast!!)
        val midMorningDishId = viewModel.getDishIdByTitle(midMorning!!)
        val lunchDishId = viewModel.getDishIdByTitle(lunch!!)
        val afternoonDishId = viewModel.getDishIdByTitle(afternoonSnack!!)
        val dinnerDishId = viewModel.getDishIdByTitle(dinner!!)

        viewModel.checkForDuplicateAndSaveMealPlan(
            requireContext(),
            email,
            selectedDate,
            breakfast,
            midMorning,
            lunch,
            afternoonSnack,
            dinner,
            breakfastDishId,
            midMorningDishId,
            lunchDishId,
            afternoonDishId,
            dinnerDishId
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
