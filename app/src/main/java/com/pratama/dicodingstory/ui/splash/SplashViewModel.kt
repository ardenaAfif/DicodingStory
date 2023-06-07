package com.pratama.dicodingstory.ui.splash

import androidx.lifecycle.ViewModel
import com.pratama.dicodingstory.data.repository.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val authRepo: AuthRepo) :
    ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepo.getAuthToken()
}