package com.example.movie.presentation.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie.data.api.NetworkResponse
import com.example.movie.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel() {

    val popularState = MutableLiveData<MovieUiState>()
    val upcomingState = MutableLiveData<MovieUiState>()
    val trendingState = MutableLiveData<MovieUiState>()
    val nowPlayingState = MutableLiveData<MovieUiState>()

    fun getPopularMovies() {
        viewModelScope.launch {
            repository.getPopularMovies().collectLatest {
                when (it) {
                    is NetworkResponse.Loading -> popularState.value = MovieUiState.Loading
                    is NetworkResponse.Error -> it.message?.let { msg ->
                        popularState.value = MovieUiState.Error(msg)
                    }
                    is NetworkResponse.Success -> {
                        it.data?.movies?.let { list ->
                            popularState.value = MovieUiState.Success(list)
                            popularState.value = MovieUiState.ViewPager(list)
                        }
                    }
                }
            }
        }
    }

    fun getUpcomingMovies() {
        viewModelScope.launch {
            repository.getUpcomingMovies().collectLatest {
                when (it) {
                    is NetworkResponse.Loading -> upcomingState.value = MovieUiState.Loading
                    is NetworkResponse.Error -> it.message?.let { msg ->
                        upcomingState.value = MovieUiState.Error(msg)
                    }
                    is NetworkResponse.Success -> {
                        it.data?.movies?.let { list ->
                            upcomingState.value = MovieUiState.Success(list)
                        }
                    }
                }
            }
        }
    }

    fun getTrendingMovies() {
        viewModelScope.launch {
            repository.getTrendingMovies().collectLatest {
                when (it) {
                    is NetworkResponse.Loading -> trendingState.value = MovieUiState.Loading
                    is NetworkResponse.Error -> it.message?.let { msg ->
                        trendingState.value = MovieUiState.Error(msg)
                    }
                    is NetworkResponse.Success -> {
                        it.data?.movies?.let { list ->
                            trendingState.value = MovieUiState.Success(list)
                        }
                    }
                }
            }
        }
    }

    fun getNowPlayingMovies() {
        viewModelScope.launch {
            repository.getNowPlayingMovies().collectLatest {
                when (it) {
                    is NetworkResponse.Loading -> nowPlayingState.value = MovieUiState.Loading
                    is NetworkResponse.Error -> it.message?.let { msg ->
                        nowPlayingState.value = MovieUiState.Error(msg)
                    }
                    is NetworkResponse.Success -> {
                        it.data?.movies?.let { list ->
                            nowPlayingState.value = MovieUiState.Success(list)
                        }
                    }
                }
            }
        }
    }
}
