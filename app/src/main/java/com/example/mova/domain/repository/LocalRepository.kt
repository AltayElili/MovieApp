package com.example.mova.domain.repository

import com.example.mova.data.model.local.ListedContent
import com.example.mova.data.model.local.Movie
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
