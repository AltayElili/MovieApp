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
    private val trendingAdapter = MovieAdapter()
    private val nowPlayingAdapter = MovieAdapter()

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
                    topRatedAdapter.submitList(it.movieList.take(10))
                    goToSeeAllTopRated(it.movieList.toTypedArray())
                }
                is MovieUiState.ViewPager -> viewPagerAdapter.submitList(it.details.take(3))
                else -> Unit
            }
        }

        viewModel.upcomingState.observe(viewLifecycleOwner) {
            binding.progressBarNew.gone()
            when (it) {
                is MovieUiState.Loading -> binding.progressBarNew.visible()
                is MovieUiState.Error -> showFancyToast(it.message, FancyToast.ERROR)
                is MovieUiState.Success -> {
                    upcomingAdapter.submitList(it.movieList.take(10))
                    goToSeeAllUpcoming(it.movieList.toTypedArray())
                }
                else -> Unit
            }
        }

        viewModel.trendingState.observe(viewLifecycleOwner) {
            binding.progressBarTrending.gone()
            when (it) {
                is MovieUiState.Loading -> binding.progressBarTrending.visible()
                is MovieUiState.Error -> showFancyToast(it.message, FancyToast.ERROR)
                is MovieUiState.Success -> {
                    trendingAdapter.submitList(it.movieList.take(10))
                    goToSeeAllTrending(it.movieList.toTypedArray())
                }
                else -> Unit
            }
        }

        viewModel.nowPlayingState.observe(viewLifecycleOwner) {
            binding.progressBarNowPlaying.gone()
            when (it) {
                is MovieUiState.Loading -> binding.progressBarNowPlaying.visible()
                is MovieUiState.Error -> showFancyToast(it.message, FancyToast.ERROR)
                is MovieUiState.Success -> {
                    nowPlayingAdapter.submitList(it.movieList.take(10))
                    goToSeeAllNowPlaying(it.movieList.toTypedArray())
                }
                else -> Unit
            }
        }
    }

    private fun goToSeeAllTopRated(list: Array<Movie>) {
        binding.buttonSeeAllPopular.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToViewAllFragment(list, "topRated")
            )
        }
    }

    private fun goToSeeAllUpcoming(list: Array<Movie>) {
        binding.buttonSeeAllUpcoming.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToViewAllFragment(list, "upcoming")
            )
        }
    }

    private fun goToSeeAllTrending(list: Array<Movie>) {
        binding.buttonSeeAllTrending.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToViewAllFragment(list, "trending")
            )
        }
    }

    private fun goToSeeAllNowPlaying(list: Array<Movie>) {
        binding.buttonSeeAllNowPlaying.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToViewAllFragment(list, "nowPlaying")
            )
        }
    }

    private fun setAdapters() {
        binding.rvTop10.adapter = topRatedAdapter
        binding.rvNewReleases.adapter = upcomingAdapter
        binding.rvTrending.adapter = trendingAdapter
        binding.rvNowPlaying.adapter = nowPlayingAdapter
        binding.viewPagerHome.adapter = viewPagerAdapter
    }

    private fun setupClicks() {
        topRatedAdapter.onClick = { id, _ ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(id, true)
            )
        }
        upcomingAdapter.onClick = { id, _ ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(id, true)
            )
        }
        trendingAdapter.onClick = { id, _ ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(id, true)
            )
        }
        nowPlayingAdapter.onClick = { id, _ ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(id, true)
            )
        }
    }



    private fun getMovies() {
        viewModel.getPopularMovies()
        viewModel.getUpcomingMovies()
        viewModel.getTrendingMovies()
        viewModel.getNowPlayingMovies()
    }
}
