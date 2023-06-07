package com.pratama.dicodingstory.ui.maps

import androidx.paging.ExperimentalPagingApi
import com.pratama.dicodingstory.data.repository.StoryRepo
import com.pratama.dicodingstory.data.remote.response.StoriesResponse
import com.pratama.dicodingstory.utils.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LocationViewModelTest {

    @Mock
    private lateinit var storyRepo: StoryRepo
    private lateinit var locationViewModel: LocationViewModel

    private val dummyStoriesResponse = DataDummy.generateDummyStoriesResponse()
    private val dummyToken = "AUTH_TOKEN"

    @Before
    fun setup() {
        locationViewModel = LocationViewModel(storyRepo)
    }

    @Test
    fun `Get story with location successfully - result success`(): Unit = runTest {

        val expectedResponse = flowOf(Result.success(dummyStoriesResponse))

        `when`(locationViewModel.getAllStories(dummyToken)).thenReturn(expectedResponse)

        locationViewModel.getAllStories(dummyToken).collect { actualResponse ->

            Assert.assertTrue(actualResponse.isSuccess)
            Assert.assertFalse(actualResponse.isFailure)

            actualResponse.onSuccess { storiesResponse ->
                Assert.assertNotNull(storiesResponse)
                Assert.assertSame(storiesResponse, dummyStoriesResponse)
            }
        }

        Mockito.verify(storyRepo).getAllStoriesWithLocation(dummyToken)
    }

    @Test
    fun `Get story with location failed - result failure with exception`(): Unit = runTest {

        val expectedResponse: Flow<Result<StoriesResponse>> =
            flowOf(Result.failure(Exception("Failed")))

        `when`(locationViewModel.getAllStories(dummyToken)).thenReturn(expectedResponse)

        locationViewModel.getAllStories(dummyToken).collect { actualResponse ->

            Assert.assertFalse(actualResponse.isSuccess)
            Assert.assertTrue(actualResponse.isFailure)

            actualResponse.onFailure {
                Assert.assertNotNull(it)
            }
        }

        Mockito.verify(storyRepo).getAllStoriesWithLocation(dummyToken)
    }
}