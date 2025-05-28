package com.example.movie.presentation

import android.os.Bundle
import com.example.movie.R
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.movie.databinding.ActivityMainBinding
import com.example.movie.utils.gone
import com.example.movie.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        val navController = navHostFragment?.navController
        val bottomMenu = binding.bottomMenu
        if (navController != null) {
            bottomMenu.setupWithNavController(navController)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> bottomMenu.visible()
                R.id.exploreFragment -> bottomMenu.visible()
                R.id.listFragment -> bottomMenu.visible()
                R.id.profileFragment -> bottomMenu.visible()
                else -> bottomMenu.gone()
            }
        }

    }
}