package com.example.movie.presentation.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie.local.LocalRepository
import com.example.movie.local.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(private val localRepository: LocalRepository) :
    ViewModel() {

    val movieList = MutableLiveData<List<Movie>>()
    val searchList = MutableLiveData<List<Movie>>()

    fun addContentToList(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.addContent(movie)
        }
    }

    fun getAllContent() {
        viewModelScope.launch {
            localRepository.getAllContent().collectLatest {
                movieList.value = it
            }
        }
    }

    fun searchMyList(query: String) {
        viewModelScope.launch {
            localRepository.searchList(query).collectLatest {
                searchList.value = it
            }
        }
    }

    fun deleteContent(movie: Movie) {
        viewModelScope.launch {
            localRepository.deleteContent(movie)
            getAllContent()
        }
    }

}