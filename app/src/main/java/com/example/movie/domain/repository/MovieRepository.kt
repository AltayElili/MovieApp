package com.example.movie.domain.repository

import com.example.movie.data.api.NetworkResponse
import com.example.movie.data.model.remote.*
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    suspend fun getPopularMovies(): Flow<NetworkResponse<MovieResponse>>

    suspend fun getUpcomingMovies(): Flow<NetworkResponse<MovieResponse>>

    suspend fun getRatedMovies(): Flow<NetworkResponse<MovieResponse>>

    suspend fun getRatedTvSeries(): Flow<NetworkResponse<TvSeriesResponse>>

    suspend fun getTrendingMovies(): Flow<NetworkResponse<MovieResponse>>

    suspend fun getNowPlayingMovies(): Flow<NetworkResponse<MovieResponse>>

    suspend fun searchMovies(query: String): Flow<NetworkResponse<MovieResponse>>

    suspend fun searchTvSeries(query: String): Flow<NetworkResponse<TvSeriesResponse>>

    suspend fun getMovieDetails(id: String): Flow<NetworkResponse<DetailResponse>>

    suspend fun getTvSeriesDetails(id: String): Flow<NetworkResponse<TvDetailsResponse>>

    suspend fun getMovieActors(id: String): Flow<NetworkResponse<ActorResponse>>

    suspend fun getTvActors(id: String): Flow<NetworkResponse<ActorResponse>>

    suspend fun getRelatedMovies(id: String): Flow<NetworkResponse<MovieResponse>>

    suspend fun getRelatedTvSeries(id: String): Flow<NetworkResponse<TvSeriesResponse>>

    suspend fun getMovieReviews(id: String): Flow<NetworkResponse<ReviewResponse>>

    suspend fun getTvSeriesReviews(id: String): Flow<NetworkResponse<ReviewResponse>>

    suspend fun getNotifications(): QuerySnapshot

    suspend fun getMovieTrailerKey(id: String): String?
}
