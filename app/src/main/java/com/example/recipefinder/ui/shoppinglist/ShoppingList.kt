package com.example.recipefinder.ui.shoppinglist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipefinder.adaptor.ShoppingListAdapter
import com.example.recipefinder.roomdb.AppDatabase
import com.example.recipefinder.roomdb.shopping.ShoppingListDao
import com.example.recipefinder.databinding.FragmentShoppingListBinding

class ShoppingList : Fragment() {

    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ShoppingListViewModel
    private lateinit var adapter: ShoppingListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        val dao: ShoppingListDao = AppDatabase.getDatabase(requireContext()).shoppingListDao()
        val factory = ShoppingListViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory).get(ShoppingListViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ShoppingListAdapter(emptyList()) { item, isChecked ->
            item.copy(isChecked = isChecked).also { updatedItem ->
                viewModel.updateItem(updatedItem)
            }
        }

        binding.recyclerViewIngredients.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewIngredients.adapter = adapter

        viewModel.shoppingListItems.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items ?: emptyList())
            binding.noItemsFound.visibility = if (items.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        binding.clearAll.setOnClickListener {
            showClearAllConfirmationDialog()
        }
    }

    private fun showClearAllConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear All Items")
            .setMessage("Are you sure you want to clear all items from the shopping list?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.clearAllShoppingListItems()
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

