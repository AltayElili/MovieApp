package com.example.movie.presentation.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie.data.model.local.Movie
import com.example.movie.domain.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private fun com.example.movie.data.model.remote.Movie.toLocal() = Movie(
        poster    = posterPath.toString(),
        rating    = voteAverage ?: 0.0,
        title     = title ?: "",
        contentId = id.toString()
    )
    fun toggleContent(remote: com.example.movie.data.model.remote.Movie, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val exists = localRepository.isContentInList(remote.id.toString()) > 0
            if (exists) {
                localRepository.deleteContent(remote.toLocal())
            } else {
                localRepository.addContent(remote.toLocal())
            }
            withContext(Dispatchers.Main) { onResult(!exists) }
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