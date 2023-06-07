package com.pratama.dicodingstory.ui.maps

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.pratama.dicodingstory.data.repository.StoryRepo
import com.pratama.dicodingstory.data.remote.response.StoriesResponse
import com.pratama.dicodingstory.utils.wrapEspressoIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class LocationViewModel @Inject constructor(private val storyRepo: StoryRepo) : ViewModel() {

    fun getAllStories(token: String): Flow<Result<StoriesResponse>> =
        storyRepo.getAllStoriesWithLocation(token)

//    fun getAllStories(token: String): Flow<Result<StoriesResponse>> = storyRepo.getAllStoriesWithLocation(token)
//        .onStart {
//            Log.d(TAG, "Fetching stories...")
//        }
//        .catch { e ->
//            Log.e(TAG, "Error fetching stories: ${e.message}")
//            emit(Result.failure(e))
//        }

    companion object {
        private const val TAG = "MapsActivity"
    }

}