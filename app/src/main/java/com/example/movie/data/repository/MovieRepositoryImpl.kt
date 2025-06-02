package com.example.movie.data.repository

import com.example.movie.api.MovieService
import com.example.movie.data.api.NetworkResponse
import com.example.movie.data.model.remote.MovieResponse
import com.example.movie.data.model.remote.TvSeriesResponse
import com.example.movie.domain.repository.MovieRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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
) : MovieRepository {

    override suspend fun getPopularMovies(): Flow<NetworkResponse<MovieResponse>> =
        safeApiRequest { service.getPopularMovies() }

    override suspend fun getUpcomingMovies(): Flow<NetworkResponse<MovieResponse>> =
        safeApiRequest { service.getUpcomingMovies() }

    override suspend fun getRatedMovies(): Flow<NetworkResponse<MovieResponse>> =
        safeApiRequest { service.getTopRatedMovies() }

    override suspend fun getRatedTvSeries(): Flow<NetworkResponse<TvSeriesResponse>> =
        safeApiRequest { service.getTopRatedTvSeries() }

    override suspend fun getTrendingMovies(): Flow<NetworkResponse<MovieResponse>> =
        safeApiRequest { service.getTrendingMovies() }

    override suspend fun getNowPlayingMovies(): Flow<NetworkResponse<MovieResponse>> =
        safeApiRequest { service.getNowPlayingMovies() }

    override suspend fun searchMovies(query: String): Flow<NetworkResponse<MovieResponse>> =
        safeApiRequest { service.searchMovies(query) }

    override suspend fun searchTvSeries(query: String): Flow<NetworkResponse<TvSeriesResponse>> =
        safeApiRequest { service.searchTvSeries(query) }

    override suspend fun getMovieDetails(id: String): Flow<NetworkResponse<com.example.movie.data.model.remote.DetailResponse>> =
        safeApiRequest { service.getMovieDetails(id) }

    override suspend fun getTvSeriesDetails(id: String): Flow<NetworkResponse<com.example.movie.data.model.remote.TvDetailsResponse>> =
        safeApiRequest { service.getTvSeriesDetails(id) }

    override suspend fun getMovieActors(id: String): Flow<NetworkResponse<com.example.movie.data.model.remote.ActorResponse>> =
        safeApiRequest { service.getMovieActors(id) }

    override suspend fun getTvActors(id: String): Flow<NetworkResponse<com.example.movie.data.model.remote.ActorResponse>> =
        safeApiRequest { service.getTvActors(id) }

    override suspend fun getRelatedMovies(id: String): Flow<NetworkResponse<MovieResponse>> =
        safeApiRequest { service.getRelatedMovies(id) }

    override suspend fun getRelatedTvSeries(id: String): Flow<NetworkResponse<TvSeriesResponse>> =
        safeApiRequest { service.getRelatedTvSeries(id) }

    override suspend fun getMovieReviews(id: String): Flow<NetworkResponse<com.example.movie.data.model.remote.ReviewResponse>> =
        safeApiRequest { service.getMovieReviews(id) }

    override suspend fun getTvSeriesReviews(id: String): Flow<NetworkResponse<com.example.movie.data.model.remote.ReviewResponse>> =
        safeApiRequest { service.getTvSeriesReviews(id) }

    override suspend fun getNotifications(): QuerySnapshot {
        return fireStore.collection("movieNotifications").get().await()
    }


    private fun <T> safeApiRequest(apicall: suspend () -> Response<T>): Flow<NetworkResponse<T>> =
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