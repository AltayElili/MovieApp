package com.example.mova.data.repository

import com.example.mova.api.MovieService
import com.example.mova.api.NetworkResponse
import com.example.mova.domain.repository.MovieRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val service: MovieService,
    private val fireStore: FirebaseFirestore
) :MovieRepository {

    override suspend fun getPopularMovies() = safeApiRequest {
        service.getPopularMovies()
    }

    override suspend fun getUpcomingMovies() = safeApiRequest {
        service.getUpcomingMovies()
    }

    override suspend fun getRatedMovies() = safeApiRequest {
        service.getTopRatedMovies()
    }

    override suspend fun getRatedTvSeries() = safeApiRequest {
        service.getTopRatedTvSeries()
    }

    override suspend fun searchMovies(query: String) = safeApiRequest {
        service.searchMovies(query)
    }

    override suspend fun searchTvSeries(query: String) = safeApiRequest {
        service.searchTvSeries(query)
    }

    override suspend fun getMovieDetails(id: String) = safeApiRequest {
        service.getMovieDetails(id)
    }

    override suspend fun getTvSeriesDetails(id: String) = safeApiRequest {
        service.getTvSeriesDetails(id)
    }

    override suspend fun getMovieActors(id: String) = safeApiRequest {
        service.getMovieActors(id)
    }

    override suspend fun getTvActors(id: String) = safeApiRequest {
        service.getTvActors(id)
    }

    override suspend fun getRelatedMovies(id: String) = safeApiRequest {
        service.getRelatedMovies(id)
    }

    override suspend fun getRelatedTvSeries(id: String) = safeApiRequest {
        service.getRelatedTvSeries(id)
    }

    override suspend fun getMovieReviews(id: String) = safeApiRequest {
        service.getMovieReviews(id)
    }

    override suspend fun getTvSeriesReviews(id: String) = safeApiRequest {
        service.getTvSeriesReviews(id)
    }

    override suspend fun getNotifications() = fireStore.collection("movieNotifications")
        .get().await()

    private suspend fun <T> safeApiRequest(apicall: suspend () -> Response<T>): Flow<NetworkResponse<T>> =
        flow<NetworkResponse<T>> {
            emit(NetworkResponse.Loading())
            try {
                val response = apicall.invoke()
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(NetworkResponse.Success(it))
                    } ?: run { emit(NetworkResponse.Error("is empty.")) }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    emit(NetworkResponse.Error(errorBody))
                }
            } catch (e: Exception) {
                emit(NetworkResponse.Error(e.localizedMessage.orEmpty()))
            }
        }.flowOn(Dispatchers.IO)
}