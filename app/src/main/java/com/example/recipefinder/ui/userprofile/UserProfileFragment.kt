package com.example.recipefinder.ui.userprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.recipefinder.R
import com.example.recipefinder.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            binding.UserName.text = user.displayName ?: "Name not available"
            binding.UserEmail.text = user.email ?: "Email not available"
            user.photoUrl?.let {
                Glide.with(this).load(it).placeholder(R.drawable.loading).error(R.drawable.error)
                    .into(binding.userImage)
            }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
