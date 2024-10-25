package com.example.recipefinder

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.recipefinder.databinding.ActivitySplashScreen2Binding
import com.example.recipefinder.ui.home.HomeFragment
import com.example.recipefinder.ui.splashscreen.SplashScreen

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreen2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreen2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val splash = SplashScreen()
        supportFragmentManager.beginTransaction()
            .replace(binding.main.id, splash)
            .addToBackStack(null)
            .commit()

    }
}