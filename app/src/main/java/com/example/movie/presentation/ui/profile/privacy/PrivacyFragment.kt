package com.example.movie.presentation.ui.profile.privacy

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.movie.db.BaseFragment
import com.example.movie.databinding.FragmentPrivacyBinding

class PrivacyFragment : BaseFragment<FragmentPrivacyBinding>(FragmentPrivacyBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageViewPrivacyToHome.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}