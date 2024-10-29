package com.example.recipefinder

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.recipefinder.databinding.ActivityHomeScreenBinding
import com.example.recipefinder.databinding.NavHeaderHomeScreenBinding
import com.google.firebase.auth.FirebaseAuth

class HomeScreen : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeScreenBinding
    private lateinit var fragmentNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentNameTextView = binding.appBarHomeScreen.fragmentNameTextView


        val navController = findNavController(R.id.nav_host_fragment_content_home_screen)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView = binding.navView

        val header = navView.getHeaderView(0)
        val bindingHeader = NavHeaderHomeScreenBinding.bind(header)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Set the user's name, email, and profile picture in the header
            bindingHeader.UserName.text = user.displayName ?: "Name not available"
            user.photoUrl?.let {
                Glide.with(this).load(it).placeholder(R.drawable.loading).error(R.drawable.error).into(bindingHeader.imageView)
            }
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home,R.id.nav_User, R.id.nav_gallery, R.id.nav_shopping, R.id.nav_battery, R.id.nav_logout), drawerLayout
        )

        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            fragmentNameTextView.text = destination.label
        }

        navView.setNavigationItemSelectedListener { menuItem ->

            if (menuItem.itemId == R.id.nav_logout) {
                signOutUser()
                drawerLayout.close() // Close drawer after selecting item
                true
            } else {
                navController.navigate(menuItem.itemId)
                drawerLayout.close()
                true
            }
        }

        binding.appBarHomeScreen.drawerImage.setOnClickListener {
            drawerLayout.open()
        }
    }

    private fun signOutUser() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, SplashScreen::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_screen, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home_screen)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
