package com.example.mova.domain.repository

import com.example.mova.api.NetworkResponse
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<NetworkResponse<AuthResult>>
    fun register(email: String, password: String): Flow<NetworkResponse<AuthResult>>
}