package com.example.movie.domain.repository

import com.example.movie.data.model.local.ListedContent
import com.example.movie.data.model.local.Movie
import dagger.Provides
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    fun addContent(movie: Movie)
    fun addContentId(listedContent: ListedContent)
    suspend fun deleteContentId(listedContent: ListedContent)
    suspend fun isContentInList(detailId: String): Int
    fun getAllContent(): Flow<List<Movie>>
    fun searchList(query: String): Flow<List<Movie>>
    suspend fun deleteContent(movie: Movie)
}