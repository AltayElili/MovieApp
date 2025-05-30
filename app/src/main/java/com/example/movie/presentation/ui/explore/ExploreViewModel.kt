package com.example.movie.presentation.ui.explore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie.data.api.NetworkResponse
import com.example.movie.data.model.remote.Movie
import com.example.movie.data.model.remote.TvSeries
import com.example.movie.domain.repository.MovieRepository
import com.example.movie.presentation.ui.home.MovieUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel() {

    val movieSearchState = MutableLiveData<MovieUiState>()
    val tvSearchState = MutableLiveData<TvSeriesUiState>()
    val selectedTabIndex = MutableLiveData<Int>()
    val tvQuery = MutableLiveData<String>()
    val movieQuery = MutableLiveData<String>()
    val ratedMovieState = MutableLiveData<MovieUiState>()
    val ratedTvSeriesState = MutableLiveData<TvSeriesUiState>()

    val lastMovieList = MutableLiveData<List<Movie>>()
    val lastTvSeriesList = MutableLiveData<List<TvSeries>>()

    fun getTopRatedMovies() {
        viewModelScope.launch {
            repository.getRatedMovies().collectLatest {
                when (it) {
                    is NetworkResponse.Loading -> ratedMovieState.value = MovieUiState.Loading
                    is NetworkResponse.Error -> ratedMovieState.value = it.message?.let { msg -> MovieUiState.Error(msg) }
                    is NetworkResponse.Success -> {
                        val movies = it.data?.movies ?: emptyList()
                        lastMovieList.value = movies
                        ratedMovieState.value = MovieUiState.Success(movies)
                    }
                }
            }
        }
    }

    fun getTopRatedTvSeries() {
        viewModelScope.launch {
            repository.getRatedTvSeries().collectLatest {
                when (it) {
                    is NetworkResponse.Loading -> ratedTvSeriesState.value = TvSeriesUiState.Loading
                    is NetworkResponse.Error -> ratedTvSeriesState.value = it.message?.let { msg -> TvSeriesUiState.Error(msg) }
                    is NetworkResponse.Success -> {
                        val series = it.data?.tvSeries ?: emptyList()
                        lastTvSeriesList.value = series
                        ratedTvSeriesState.value = TvSeriesUiState.Success(series)
                    }
                }
            }
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            repository.searchMovies(query).collectLatest {
                when (it) {
                    is NetworkResponse.Loading -> movieSearchState.value = MovieUiState.Loading
                    is NetworkResponse.Error -> movieSearchState.value = it.message?.let { msg -> MovieUiState.Error(msg) }
                    is NetworkResponse.Success -> {
                        val movies = it.data?.movies ?: emptyList()
                        lastMovieList.value = movies
                        movieSearchState.value = MovieUiState.Success(movies)
                    }
                }
            }
        }
    }

    fun searchTvSeries(query: String) {
        viewModelScope.launch {
            repository.searchTvSeries(query).collectLatest {
                when (it) {
                    is NetworkResponse.Loading -> tvSearchState.value = TvSeriesUiState.Loading
                    is NetworkResponse.Error -> tvSearchState.value = it.message?.let { msg -> TvSeriesUiState.Error(msg) }
                    is NetworkResponse.Success -> {
                        val series = it.data?.tvSeries ?: emptyList()
                        lastTvSeriesList.value = series
                        tvSearchState.value = TvSeriesUiState.Success(series)
                    }
                }
            }
        }
    }

    fun setSelectedTabIndex(currentTab: Int) {
        selectedTabIndex.value = currentTab
    }

    fun saveTvQuery(query: String) {
        tvQuery.value = query
    }

    fun saveMovieQuery(query: String) {
        movieQuery.value = query
    }

    sealed class TvSeriesUiState {
        data object Loading : TvSeriesUiState()
        class Error(val message: String) : TvSeriesUiState()
        class Success(val tvList: List<TvSeries>) : TvSeriesUiState()
    }
}