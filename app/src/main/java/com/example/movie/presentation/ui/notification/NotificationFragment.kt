package com.example.movie.presentation.ui.notification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.movie.databinding.FragmentNotificationBinding
import com.example.movie.db.BaseFragment
import com.example.movie.utils.gone
import com.example.movie.utils.visible
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment :
    BaseFragment<FragmentNotificationBinding>(FragmentNotificationBinding::inflate) {

    private val viewModel by viewModels<NotificationViewModel>()
    private val notificationAdapter = NotificationAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvNotifications.adapter = notificationAdapter
        observeData()
        viewModel.getNotifications()
        binding.imageViewArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeData() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            binding.progressBarNotification.gone()
            when (it) {
                is NotificationViewModel.NotificationUiState.Loading -> binding.progressBarNotification.visible()
                is NotificationViewModel.NotificationUiState.Error -> showFancyToast(
                    it.message,
                    FancyToast.ERROR
                )

                is NotificationViewModel.NotificationUiState.Success -> notificationAdapter.submitList(
                    it.notificationList
                )
            }
        }
    }

}