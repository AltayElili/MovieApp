package com.example.mova.domain.repository

import com.example.mova.api.NetworkResponse
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    suspend fun getPopularMovies(): Flow<NetworkResponse<*>>
    suspend fun getUpcomingMovies(): Flow<NetworkResponse<*>>
    suspend fun getRatedMovies(): Flow<NetworkResponse<*>>
    suspend fun getRatedTvSeries(): Flow<NetworkResponse<*>>
    suspend fun searchMovies(query: String): Flow<NetworkResponse<*>>
    suspend fun searchTvSeries(query: String): Flow<NetworkResponse<*>>
    suspend fun getMovieDetails(id: String): Flow<NetworkResponse<*>>
    suspend fun getTvSeriesDetails(id: String): Flow<NetworkResponse<*>>
    suspend fun getMovieActors(id: String): Flow<NetworkResponse<*>>
    suspend fun getTvActors(id: String): Flow<NetworkResponse<*>>
    suspend fun getRelatedMovies(id: String): Flow<NetworkResponse<*>>
    suspend fun getRelatedTvSeries(id: String): Flow<NetworkResponse<*>>
    suspend fun getMovieReviews(id: String): Flow<NetworkResponse<*>>
    suspend fun getTvSeriesReviews(id: String): Flow<NetworkResponse<*>>
    suspend fun getNotifications(): QuerySnapshot
}
