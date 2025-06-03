package com.example.movie.presentation.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
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
class RelatedFragment :
    BaseFragment<FragmentRelatedBinding>(FragmentRelatedBinding::inflate) {

    private val movieId: String by lazy {
        requireArguments().getString(ARG_MOVIE_ID) ?: ""
    }
    private val isMovie: Boolean by lazy {
        requireArguments().getBoolean(ARG_IS_MOVIE)
    }

    companion object {
        private const val ARG_MOVIE_ID = "movie_id"
        private const val ARG_IS_MOVIE = "is_movie"

        fun newInstance(id: String, isMovie: Boolean) = RelatedFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_MOVIE_ID, id)
                putBoolean(ARG_IS_MOVIE, isMovie)
            }
        }
    }

        private val viewModel: DetailViewModel by viewModels({ requireParentFragment() })
    private val adapter = MovieAdapter(true)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvRelated.apply {
            layoutManager = GridLayoutManager(
                requireContext(),
                2,
                GridLayoutManager.VERTICAL,
                false
            )
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