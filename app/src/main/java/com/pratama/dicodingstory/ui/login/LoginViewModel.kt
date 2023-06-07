package com.pratama.dicodingstory.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pratama.dicodingstory.data.local.AuthPreferencesDataSource
import com.pratama.dicodingstory.data.repository.AuthRepo
import com.pratama.dicodingstory.data.remote.response.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val authPreferencesDataSource: AuthPreferencesDataSource
) : ViewModel() {

    suspend fun userLogin(email: String, password: String): Flow<Result<LoginResponse>> =
        authRepo.userLogin(email, password)

    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            authPreferencesDataSource.saveAuthToken(token)
        }
    }
}