package com.pratama.dicodingstory.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pratama.dicodingstory.data.repository.AuthRepo
import com.pratama.dicodingstory.data.repository.StoryRepo
import com.pratama.dicodingstory.data.local.entity.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyRepo: StoryRepo,
    private val authRepo: AuthRepo
) : ViewModel() {

    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            authRepo.saveAuthToken(token)
        }
    }

    fun getAllStories(token: String): LiveData<PagingData<Story>> =
        storyRepo.getAllStories(token).cachedIn(viewModelScope).asLiveData()
}