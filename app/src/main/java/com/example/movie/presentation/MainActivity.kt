package com.example.movie.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.movie.R
import com.example.movie.databinding.ActivityMainBinding
import com.example.movie.utils.LocaleHelper
import com.example.movie.utils.gone
import com.example.movie.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun attachBaseContext(newBase: Context?) {
        val ctx = newBase?.let { LocaleHelper.setLocale(it, LocaleHelper.getSavedLanguage(it)) }
        super.attachBaseContext(ctx)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestNotificationPermission()

        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        val navController = navHost?.navController
        val bottomMenu = binding.bottomMenu

        navController?.let { bottomMenu.setupWithNavController(it) }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.exploreFragment,
                R.id.listFragment,
                R.id.profileFragment,
                R.id.detailFragment
                    -> bottomMenu.visible()
                else -> bottomMenu.gone()
            }
        }

    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    501
                )
            }
        }
    }
}
