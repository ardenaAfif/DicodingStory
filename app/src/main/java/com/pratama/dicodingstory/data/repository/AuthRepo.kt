package com.pratama.dicodingstory.data.repository

import com.pratama.dicodingstory.data.local.AuthPreferencesDataSource
import com.pratama.dicodingstory.data.remote.response.LoginResponse
import com.pratama.dicodingstory.data.remote.response.RegisterResponse
import com.pratama.dicodingstory.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepo @Inject constructor(
    private val apiService: ApiService,
    private val preferencesDataSource: AuthPreferencesDataSource
) {

    suspend fun userLogin(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.userLogin(email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun userRegister(
        name: String,
        email: String,
        password: String
    ): Flow<Result<RegisterResponse>> = flow {
        try {
            val response = apiService.userRegister(name, email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun saveAuthToken(token: String) {
        preferencesDataSource.saveAuthToken(token)
    }

    fun getAuthToken(): Flow<String?> = preferencesDataSource.getAuthToken()
}