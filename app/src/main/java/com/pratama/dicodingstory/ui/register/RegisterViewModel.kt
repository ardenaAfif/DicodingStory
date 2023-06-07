package com.pratama.dicodingstory.ui.register

import androidx.lifecycle.ViewModel
import com.pratama.dicodingstory.data.repository.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepo: AuthRepo
) : ViewModel() {

    suspend fun register(name: String, email: String, password: String) =
        authRepo.userRegister(name, email, password)
}