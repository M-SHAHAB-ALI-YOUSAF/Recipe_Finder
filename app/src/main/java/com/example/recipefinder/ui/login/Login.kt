package com.example.recipefinder.ui.login

import LoginViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.recipefinder.HomeScreen
import com.example.recipefinder.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.tashila.pleasewait.PleaseWaitDialog
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

@Suppress("DEPRECATION")
class Login : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var progressDialog: PleaseWaitDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        FacebookSdk.sdkInitialize(requireContext().applicationContext)
        AppEventsLogger.activateApp(requireActivity().application)
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // Initialize progress dialog
        progressDialog = PleaseWaitDialog(requireContext())

        // Initialize Google and Facebook sign-in
        loginViewModel.initialize(requireActivity())

        // Google Sign-In button click
        binding.googleSignin.setOnClickListener {
            val signInIntent = loginViewModel.getGoogleSignInIntent()
            progressDialog.show()
            googleSignInLauncher.launch(signInIntent)
        }

        // Facebook Sign-In button click
        binding.Facebook.setOnClickListener {
            progressDialog.show()
            loginViewModel.startFacebookLogin(requireActivity(),
                onSuccess = {
                    progressDialog.dismiss()
                    val intent = Intent(requireContext(), HomeScreen::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                },
                onFailure = { errorMessage ->
                    progressDialog.dismiss()
                    Snackbar.make(binding.root, "Sign-In Failed: $errorMessage", Snackbar.LENGTH_SHORT).show()
                }
            )
        }

        return binding.root
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        progressDialog.dismiss()
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            loginViewModel.handleGoogleSignInResult(result.data,
                onSuccess = {
                    val intent = Intent(requireContext(), HomeScreen::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                },
                onFailure = { errorMessage ->
                    Snackbar.make(binding.root, "Sign-In Failed: $errorMessage", Snackbar.LENGTH_SHORT).show()
                }
            )
        } else {
            Snackbar.make(binding.root, "Sign-In Canceled", Snackbar.LENGTH_SHORT).show()
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginViewModel.handleFacebookActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
