package com.example.movie.presentation.ui.explore

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import com.example.movie.R
import com.example.movie.data.model.remote.Movie
import com.example.movie.data.model.remote.TvSeries
import com.example.movie.db.BaseFragment
import com.example.movie.databinding.FragmentExploreBinding
import com.example.movie.presentation.ui.home.MovieAdapter
import com.example.movie.presentation.ui.home.MovieUiState
import com.example.movie.utils.gone
import com.example.movie.utils.visible
import com.google.android.material.tabs.TabLayout
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.sortedWith

@AndroidEntryPoint
class ExploreFragment : BaseFragment<FragmentExploreBinding>(FragmentExploreBinding::inflate) {

    private val viewModel by viewModels<ExploreViewModel>()
    private var currentTab: Int = 0
    private val tvSearchAdapter = TvAdapter()
    private val movieSearchAdapter = MovieAdapter(true)
    private var savedTvQuery: String = ""
    private var savedMovieQuery: String = ""
    private var searchResult: Boolean = false
    private var isAlphabetAsc = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDefaultContent()
        observeData()
        observeTab()
        setupClicks()
    }

    private fun loadDefaultContent() {
        if (currentTab == 0) {
            binding.rvExplore.adapter = movieSearchAdapter
            viewModel.getTopRatedMovies()
        } else {
            binding.rvExplore.adapter = tvSearchAdapter
            viewModel.getTopRatedTvSeries()
        }
    }

    private fun observeTab() {
        binding.tabLayoutExplore.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                viewModel.setSelectedTabIndex(currentTab)
                if (searchResult) showContentState()
                if (currentTab == 0) {
                    binding.editTextSearch.setText(savedMovieQuery)
                } else {
                    binding.editTextSearch.setText(savedTvQuery)
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabReselected(p0: TabLayout.Tab?) {}
        })
    }

    private fun observeData() {
        viewModel.movieSearchState.observe(viewLifecycleOwner) {
            binding.animationView.gone()
            when (it) {
                is MovieUiState.Loading -> binding.animationView.visible()
                is MovieUiState.Error -> showFancyToast(it.message, FancyToast.ERROR)
                is MovieUiState.Success -> {
                    sortAndSubmitMovies(it.movieList)
                }
                else -> Unit
            }
        }

        viewModel.tvSearchState.observe(viewLifecycleOwner) {
            binding.animationView.gone()
            when (it) {
                is ExploreViewModel.TvSeriesUiState.Loading -> binding.animationView.visible()
                is ExploreViewModel.TvSeriesUiState.Error -> showFancyToast(it.message, FancyToast.ERROR)
                is ExploreViewModel.TvSeriesUiState.Success -> {
                    sortAndSubmitTv(it.tvList)
                }
                else -> Unit
            }
        }

        viewModel.ratedMovieState.observe(viewLifecycleOwner) {
            binding.animationView.gone()
            when (it) {
                is MovieUiState.Loading -> binding.animationView.visible()
                is MovieUiState.Error -> showFancyToast(it.message, FancyToast.ERROR)
                is MovieUiState.Success -> {
                    sortAndSubmitMovies(it.movieList)
                }
                else -> Unit
            }
        }

        viewModel.ratedTvSeriesState.observe(viewLifecycleOwner) {
            binding.animationView.gone()
            when (it) {
                is ExploreViewModel.TvSeriesUiState.Loading -> binding.animationView.visible()
                is ExploreViewModel.TvSeriesUiState.Error -> showFancyToast(it.message, FancyToast.ERROR)
                is ExploreViewModel.TvSeriesUiState.Success -> {
                    sortAndSubmitTv(it.tvList)
                }
                else -> Unit
            }
        }

        viewModel.tvQuery.observe(viewLifecycleOwner) { savedTvQuery = it }
        viewModel.movieQuery.observe(viewLifecycleOwner) { savedMovieQuery = it }
        viewModel.selectedTabIndex.observe(viewLifecycleOwner) {
            binding.tabLayoutExplore.getTabAt(it)?.select()
        }
    }

    private fun setupClicks() {
        binding.editTextSearch.addTextChangedListener {
            val query = it.toString().trim()
            if (query.isNotEmpty()) {
                handleSearch(query)
            } else {
                handleEmptySearch()
            }
        }

        binding.buttonFilter.setOnClickListener {
            showFilterPopup(it)
        }

        movieSearchAdapter.onClick = { id, _ ->
            findNavController().navigate(
                ExploreFragmentDirections.actionExploreFragmentToDetailFragment(true, id)
            )
        }

        tvSearchAdapter.onClick = { id, _ ->
            findNavController().navigate(
                ExploreFragmentDirections.actionExploreFragmentToDetailFragment(false, id)
            )
        }
    }

    private fun handleSearch(query: String) {
        when (currentTab) {
            0 -> {
                binding.rvExplore.adapter = movieSearchAdapter
                viewModel.searchMovies(query)
                viewModel.saveMovieQuery(query)
            }

            1 -> {
                binding.rvExplore.adapter = tvSearchAdapter
                viewModel.searchTvSeries(query)
                viewModel.saveTvQuery(query)
            }
        }
    }

    private fun handleEmptySearch() {
        when (currentTab) {
            0 -> {
                binding.rvExplore.adapter = movieSearchAdapter
                viewModel.getTopRatedMovies()
                viewModel.saveMovieQuery("")
            }

            1 -> {
                binding.rvExplore.adapter = tvSearchAdapter
                viewModel.getTopRatedTvSeries()
                viewModel.saveTvQuery("")
            }
        }
    }

    private fun showFilterPopup(anchor: View) {
        val popupMenu = PopupMenu(requireContext(), anchor)
        popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.sort_az -> {
                    isAlphabetAsc = true
                    reSortList()
                    true
                }

                R.id.sort_za -> {
                    isAlphabetAsc = false
                    reSortList()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun reSortList() {
        when (currentTab) {
            0 -> {
                val currentList = viewModel.lastMovieList.value
                if (currentList.isNullOrEmpty()) {
                    viewModel.getTopRatedMovies()
                } else {
                    sortAndSubmitMovies(currentList)
                }
            }
            1 -> {
                val currentList = viewModel.lastTvSeriesList.value
                if (currentList.isNullOrEmpty()) {
                    viewModel.getTopRatedTvSeries()
                } else {
                    sortAndSubmitTv(currentList)
                }
            }
        }
    }

    private fun sortAndSubmitMovies(list: List<com.example.movie.data.model.remote.Movie>) {
        val sorted = if (isAlphabetAsc) {
            list.sortedWith(compareBy(nullsLast()) { it.title?.trim()?.lowercase() })
        } else {
            list.sortedWith(compareBy(nullsLast()) { it.title?.trim()?.lowercase() }).reversed()
        }

        handleSuccess(sorted, movieSearchAdapter)
    }


    private fun sortAndSubmitTv(list: List<com.example.movie.data.model.remote.TvSeries>) {
        val sorted = if (isAlphabetAsc) {
            list.sortedWith(compareBy(nullsLast()) { it.name?.trim()?.lowercase() })
        } else {
            list.sortedWith(compareBy(nullsLast()) { it.name?.trim()?.lowercase() }).reversed()
        }

        handleSuccess(sorted, tvSearchAdapter)
    }

    private fun <T> handleSuccess(list: List<T>, adapter: ListAdapter<T, *>) {
        searchResult = if (list.isEmpty()) {
            showEmptyState()
            true
        } else {
            showContentState()
            adapter.submitList(list)
            false
        }
    }

    private fun showEmptyState() {
        binding.errorMessage.visible()
        binding.rvExplore.gone()
    }

    private fun showContentState() {
        binding.errorMessage.gone()
        binding.rvExplore.visible()
    }
}
