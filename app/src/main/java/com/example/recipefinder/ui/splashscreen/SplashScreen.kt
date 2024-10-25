package com.example.recipefinder.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.recipefinder.HomeScreen
import com.example.recipefinder.R
import com.example.recipefinder.databinding.FragmentSplashScreenBinding
import com.example.recipefinder.ui.login.Login
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : Fragment() {
    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val splashScreenmodel =
            ViewModelProvider(this).get(SplashScreenViewModel::class.java)

        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        val root: View = binding.root


        auth = FirebaseAuth.getInstance()


        val currentUser = auth.currentUser
        binding.getStarted.setOnClickListener {
        if (currentUser != null) {
            val intent = Intent(requireContext(), HomeScreen::class.java)
            startActivity(intent)
            requireActivity().finish()
        } else {

                val loginFragment = Login()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main, loginFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
