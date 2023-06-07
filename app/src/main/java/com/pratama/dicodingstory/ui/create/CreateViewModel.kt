package com.pratama.dicodingstory.ui.create

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.pratama.dicodingstory.data.repository.AuthRepo
import com.pratama.dicodingstory.data.repository.StoryRepo
import com.pratama.dicodingstory.data.remote.response.FileUploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class CreateViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val storyRepo: StoryRepo
) : ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepo.getAuthToken()

    suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Flow<Result<FileUploadResponse>> =
        storyRepo.uploadImage(token, file, description, lat, lon)
}