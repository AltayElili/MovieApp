package com.example.mova.data.repository

import com.example.mova.api.NetworkResponse
import com.example.mova.domain.repository.AuthRepository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : AuthRepository {

    override fun login(email: String, password: String): Flow<NetworkResponse<AuthResult>> =
        flow {
            emit(NetworkResponse.Loading())
            val userLogin = auth.signInWithEmailAndPassword(email, password).await()
            emit(NetworkResponse.Success(userLogin))
        }.catch {
            emit(NetworkResponse.Error(it.message.toString()))
        }

    override fun register(email: String, password: String): Flow<NetworkResponse<AuthResult>> =
        flow {
            emit(NetworkResponse.Loading())
            val userRegister = auth.createUserWithEmailAndPassword(email, password).await()
            emit(NetworkResponse.Success(userRegister))
        }.catch {
            emit(NetworkResponse.Error(it.message.orEmpty()))
        }
}