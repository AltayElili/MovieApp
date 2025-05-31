package com.example.movie.presentation.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.movie.db.BaseFragment
import com.example.movie.databinding.FragmentHomeBinding
import com.example.movie.data.model.remote.Movie
import com.example.movie.presentation.ui.list.ListViewModel
import com.example.movie.utils.gone
import com.example.movie.utils.visible
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel by viewModels<HomeViewModel>()
    private val listViewModel by activityViewModels<ListViewModel>()
    private val topRatedAdapter = MovieAdapter()
    private val upcomingAdapter = MovieAdapter()
    private val viewPagerAdapter = HomePagerAdapter { movie, action ->
        when (action) {
            HeroAction.PLAY -> {
                val url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
            HeroAction.ADD -> {
                listViewModel.toggleContent(movie) { added ->
                    val msg = if (added) "Added to My List" else "Removed from My List"
                    showFancyToast(msg, FancyToast.INFO)
                }
            }
            HeroAction.NOTIFICATION -> {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToNotificationFragment()
                )
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapters()
        observeData()
        getMovies()
        setupClicks()

    }


    private fun observeData() {
        viewModel.popularState.observe(viewLifecycleOwner) {
            binding.progressBarTop10.gone()
            when (it) {
                is MovieUiState.Loading -> binding.progressBarTop10.visible()
                is MovieUiState.Error -> showFancyToast(it.message, FancyToast.ERROR)
                is MovieUiState.Success -> {
                    topRatedAdapter.submitList(
                        it.movieList.take(
                            10
                        )
                    )
                    val popularList = it.movieList
                    goToSeeAllTopRated(popularList.toTypedArray())
                }

                is MovieUiState.ViewPager -> viewPagerAdapter.submitList(it.details.take(3))
                else -> Unit
            }
        }

        viewModel.upcomingState.observe(viewLifecycleOwner) {
            binding.progressBarNew.gone()
            when (it) {
                is MovieUiState.Loading -> binding.progressBarNew.visible()
                is MovieUiState.Error -> showFancyToast(
                    it.message,
                    FancyToast.ERROR
                )

                is MovieUiState.Success -> {
                    upcomingAdapter.submitList(
                        it.movieList.take(
                            10
                        )
                    )
                    val upcomingList = it.movieList
                    goToSeeAllUpcoming(
                        upcomingList.toTypedArray()
                    )
                }

                else -> Unit
            }
        }
    }

    private fun goToSeeAllUpcoming(list: Array<Movie>) {
        binding.buttonSeeAllUpcoming.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToViewAllFragment(
                    list, "upcoming"
                )
            )
        }
    }

    private fun goToSeeAllTopRated(list: Array<Movie>) {
        binding.buttonSeeAllPopular.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToViewAllFragment(
                    list, "topRated"
                )
            )
        }
    }

    private fun setAdapters() {
        binding.rvTop10.adapter = topRatedAdapter
        binding.rvNewReleases.adapter = upcomingAdapter
        binding.viewPagerHome.adapter = viewPagerAdapter
    }

    private fun setupClicks() {
        topRatedAdapter.onClick = { id, _ ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(true, id)
            )
        }
        upcomingAdapter.onClick = { id, _ ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(true, id)
            )
        }
    }

    private fun getMovies() {
        viewModel.getPopularMovies()
        viewModel.getUpcomingMovies()
    }


}