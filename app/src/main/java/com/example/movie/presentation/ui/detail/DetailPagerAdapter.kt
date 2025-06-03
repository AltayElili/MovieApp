package com.example.movie.presentation.ui.detail

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class DetailPagerAdapter(
    fragment: Fragment,
    private val movieId: String,
    private val isMovie: Boolean
) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RelatedFragment.newInstance(movieId, isMovie)
            1 -> ReviewFragment(movieId, isMovie)
            else -> throw IllegalStateException("Invalid position")
        }
    }
}