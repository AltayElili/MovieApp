package com.example.movie.presentation.ui.profile.myProfile

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.movie.base.BaseFragment
import com.example.movie.databinding.FragmentProfileNotificationBinding

class ProfileNotificationFragment :
    BaseFragment<FragmentProfileNotificationBinding>(FragmentProfileNotificationBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageViewNotificationToHome.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}