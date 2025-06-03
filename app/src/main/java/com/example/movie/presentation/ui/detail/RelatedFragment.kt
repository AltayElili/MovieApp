package com.example.movie.presentation.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movie.db.BaseFragment
import com.example.movie.databinding.FragmentRelatedBinding
import com.example.movie.presentation.ui.home.MovieAdapter
import com.example.movie.presentation.ui.home.MovieUiState
import com.example.movie.utils.gone
import com.example.movie.utils.visible
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RelatedFragment(private val movieId: String, private val isMovie: Boolean) :
    BaseFragment<FragmentRelatedBinding>(FragmentRelatedBinding::inflate) {

    private val viewModel: DetailViewModel by viewModels({ requireParentFragment() })
    private val adapter = MovieAdapter(true)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvRelated.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = this@RelatedFragment.adapter
        }

        adapter.onClick = { id, _ ->
            requireParentFragment().findNavController()
                .navigate(DetailFragmentDirections.actionDetailFragmentSelf(id, isMovie))
            viewModel.clearStates()
        }
        observeData()
        getRecommendations()
    }

    private fun observeData() {
        viewModel.relatedMovieState.observe(viewLifecycleOwner) {
            binding.animationViewPager.gone()
            when (it) {
                is MovieUiState.Loading -> binding.animationViewPager.visible()
                is MovieUiState.Error -> showFancyToast(it.message, FancyToast.ERROR)
                is MovieUiState.Success -> {
                    if (it.movieList.isNotEmpty()) {
                        adapter.submitList(it.movieList)
                    } else binding.textViewNoMovie.visible()
                }

                else -> {}
            }
        }
        viewModel.relatedTvState.observe(viewLifecycleOwner) {
            binding.animationViewPager.gone()
            when (it) {
                is MovieUiState.Loading -> binding.animationViewPager.visible()
                is MovieUiState.Error -> showFancyToast(it.message, FancyToast.ERROR)
                is MovieUiState.Success -> {
                    if (it.movieList.isNotEmpty()) {
                        adapter.submitList(it.movieList)
                    } else binding.textViewNoMovie.visible()
                }

                else -> {}
            }
        }
    }

    private fun getRecommendations() {
        if (isMovie) {
            viewModel.getRelatedMovies(movieId)
        } else {
            viewModel.getRelatedTvSeries(movieId)
        }
    }
}