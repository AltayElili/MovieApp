package com.example.movie.presentation.ui.auth

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.movie.databinding.FragmentAuthBinding
import com.example.movie.base.BaseFragment

class AuthFragment : BaseFragment<FragmentAuthBinding>(FragmentAuthBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClicks()
    }

    private fun setupClicks() {
        binding.imageViewArrow.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonAuthSignIn.setOnClickListener {
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToLoginFragment())
        }
        binding.buttonAuthSignUp.setOnClickListener {
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToRegisterFragment())
        }
    }

}