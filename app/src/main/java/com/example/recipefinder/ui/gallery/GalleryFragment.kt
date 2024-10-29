package com.example.recipefinder.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipefinder.R
import com.example.recipefinder.adaptor.SavedDishAdapter
import com.example.recipefinder.databinding.FragmentGalleryBinding
import com.example.recipefinder.roomdb.AppDatabase
import com.example.recipefinder.roomdb.DishDao
import com.example.recipefinder.roomdb.favorite.SavedDishDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var dishDao: DishDao
    private lateinit var savedDishDao: SavedDishDao
    private lateinit var savedDishAdapter: SavedDishAdapter
    private var job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)

        val database = AppDatabase.getDatabase(requireContext())
        dishDao = database.dishDao()
        savedDishDao = database.savedDishDao()

        val drawerImage = requireActivity().findViewById<ImageView>(R.id.drawer_image)
        val fragmentNameTextView = requireActivity().findViewById<TextView>(R.id.fragment_name_text_view)
        drawerImage?.visibility = View.VISIBLE
        fragmentNameTextView?.visibility = View.VISIBLE

        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchSavedDishes()
    }

    private fun setupRecyclerView() {
        savedDishAdapter = SavedDishAdapter(requireContext(), emptyList()) { isEmpty ->
            binding.noFavoriteFound.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
        binding.recyclerViewFavorite.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFavorite.adapter = savedDishAdapter
    }

    private fun fetchSavedDishes() {
        coroutineScope.launch {
            val savedDishIds = savedDishDao.getSavedDishIds()
            if (savedDishIds.isNotEmpty()) {
                val dishes = withContext(Dispatchers.IO) {
                    dishDao.getDishesByIds(savedDishIds)
                }
                savedDishAdapter.updateDishes(dishes)
                binding.noFavoriteFound.visibility = View.GONE
            } else {
                binding.noFavoriteFound.visibility = View.VISIBLE
                savedDishAdapter.updateDishes(emptyList())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
