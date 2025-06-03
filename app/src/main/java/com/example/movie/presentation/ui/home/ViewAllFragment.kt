package com.example.movie.presentation.ui.home

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.movie.R
import com.example.movie.db.BaseFragment
import com.example.movie.databinding.FragmentViewAllBinding
import com.example.movie.data.model.remote.Movie
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.toList

@AndroidEntryPoint
class ViewAllFragment : BaseFragment<FragmentViewAllBinding>(FragmentViewAllBinding::inflate) {

    private val args: ViewAllFragmentArgs by navArgs()
    private val adapter = MovieAdapter(true)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvAllMovies.adapter = adapter
        setAllMovies(args.movies.toList())
        if (args.title == "upcoming") {
            binding.textViewHeader.text = getString(R.string.new_releases)
        }
        setupClicks()
    }

    private fun setAllMovies(list: List<Movie>) {
        adapter.submitList(list)
    }

    private fun setupClicks() {
        adapter.onClick = { id, _ ->
            findNavController().navigate(
                ViewAllFragmentDirections.actionViewAllFragmentToDetailFragment(
                    id, true
                )
            )
        }
        binding.imageViewBackToHome.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}