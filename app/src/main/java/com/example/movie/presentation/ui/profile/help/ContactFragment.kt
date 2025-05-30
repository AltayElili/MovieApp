package com.example.movie.presentation.ui.profile.help

import android.os.Bundle
import android.view.View
import com.example.movie.R
import com.example.movie.db.BaseFragment
import com.example.movie.databinding.FragmentContactBinding

class ContactFragment : BaseFragment<FragmentContactBinding>(FragmentContactBinding::inflate) {

    private val contactAdapter = ContactAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvContactUs.adapter = contactAdapter
        setUpContacts()
    }

    private fun setUpContacts() {
        contactAdapter.submitList(
            listOf(
                Contact("Customer Service", R.drawable.headphone),
                Contact("WhatsApp", R.drawable.whatsapp),
                Contact("Website", R.drawable.web),
                Contact("Facebook", R.drawable.facebook),
                Contact("Twitter", R.drawable.twitter),
                Contact("Instagram", R.drawable.instagram)
            )
        )
    }

}