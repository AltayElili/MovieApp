package com.example.mova.presentation.ui.auth

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mova.api.NetworkResponse
import com.example.mova.data.repository.AuthRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepositoryImpl: AuthRepositoryImpl,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val uiState = MutableLiveData<LoginUiState>()


    fun loginUser(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                authRepositoryImpl.login(email, password).collectLatest {
                    when (it) {
                        is NetworkResponse.Loading -> uiState.value = LoginUiState.Loading
                        is NetworkResponse.Error -> it.message?.let {
                            uiState.value = LoginUiState.Error(it)
                        }

                        is NetworkResponse.Success -> it.data?.user?.email?.let {
                            uiState.value = LoginUiState.Success(it)
                            saveEmailToPreferences(it)
                        }
                    }
                }
            }
        } else uiState.value = LoginUiState.EmptyFields
    }

    fun putSharedPref() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("registered", true)
        editor.apply()
    }

    private fun saveEmailToPreferences(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("user_email", email)
        editor.apply()
    }


    sealed class LoginUiState {
        data class Success(val email: String) : LoginUiState()
        data class Error(val message: String) : LoginUiState()
        data object Loading : LoginUiState()
        data object EmptyFields : LoginUiState()
    }

}