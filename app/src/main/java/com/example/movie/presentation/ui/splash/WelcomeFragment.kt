package com.example.movie.presentation.ui.splash

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.movie.databinding.FragmentWelcomeBinding
import com.example.movie.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>(FragmentWelcomeBinding::inflate) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readSharedPref()
        binding.buttonStart.setOnClickListener {
            findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToAuthFragment())
        }
    }

    private fun readSharedPref() {
        val login = sharedPreferences.getBoolean("registered", false)
        if (login) findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToHomeFragment())
    }

}