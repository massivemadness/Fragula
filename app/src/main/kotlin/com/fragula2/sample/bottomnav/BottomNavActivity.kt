package com.fragula2.sample.bottomnav

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.fragula2.sample.databinding.ActivityBottomnavBinding

class BottomNavActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomnavBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomnavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = binding.navHost.getFragment<NavHostFragment>()
            .navController

        binding.bottomNav.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}