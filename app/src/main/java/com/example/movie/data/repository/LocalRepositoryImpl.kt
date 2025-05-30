package com.example.movie.data.repository

import com.example.movie.data.local.MovieDAO
import com.example.movie.data.model.local.ListedContent
import com.example.movie.data.model.local.Movie
import com.example.movie.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val dao: MovieDAO
) : LocalRepository {

    override fun addContent(movie: Movie) = dao.addContent(movie)

    override fun addContentId(listedContent: ListedContent) = dao.addListedContentId(listedContent)

    override suspend fun deleteContentId(listedContent: ListedContent) =
        dao.deleteListedContentId(listedContent)

    override suspend fun isContentInList(detailId: String): Int =
        dao.isContentInList(detailId)

    override fun getAllContent(): Flow<List<Movie>> = dao.getAllContent()

    override fun searchList(query: String): Flow<List<Movie>> = dao.searchList(query)

    override suspend fun deleteContent(movie: Movie) = dao.deleteContent(movie)
}