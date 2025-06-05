package com.example.movie.presentation.ui.profile.help

import android.os.Bundle
import android.view.View
import com.example.movie.R
import com.example.movie.db.BaseFragment
import com.example.movie.databinding.FragmentFaqBinding
import com.example.movie.utils.gone
import com.example.movie.utils.visible

class FaqFragment : BaseFragment<FragmentFaqBinding>(FragmentFaqBinding::inflate) {

    private val faqAdapter = FAQAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFAQ.adapter = faqAdapter
        getFaqList()
        setUpClicks()

    }

    private fun setUpClicks() {
        faqAdapter.onClick = {
            it.imageViewDropdown.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    it.textViewFaqAnswer.visible()
                } else {
                    it.textViewFaqAnswer.gone()
                }
            }
        }
    }

    private fun getFaqList() {
        faqAdapter.submitList(
            listOf(
                FAQ(getString(R.string.faq_what_is_netflix), getString(R.string.what_is_movie)),
                FAQ(getString(R.string.faq_how_to_add), getString(R.string.how_to_add_content)),
                FAQ(getString(R.string.faq_how_to_delete), getString(R.string.how_to_delete_content)),
                FAQ(getString(R.string.faq_how_to_change_user), getString(R.string.how_to_change_user_info)),
                FAQ(getString(R.string.faq_how_to_subscribe), getString(R.string.lorem)),
                FAQ(getString(R.string.faq_how_to_contact), getString(R.string.lorem)),
                FAQ(getString(R.string.faq_payment_methods), getString(R.string.lorem))
            )
        )
    }

}