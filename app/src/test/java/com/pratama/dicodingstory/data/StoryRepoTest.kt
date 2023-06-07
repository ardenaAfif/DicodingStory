package com.pratama.dicodingstory.data

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ListUpdateCallback
import com.pratama.dicodingstory.adapter.StoryListAdapter
import com.pratama.dicodingstory.data.local.room.StoryDatabase
import com.pratama.dicodingstory.data.remote.response.StoriesResponse
import com.pratama.dicodingstory.data.remote.retrofit.ApiService
import com.pratama.dicodingstory.data.repository.StoryRepo
import com.pratama.dicodingstory.utils.CoroutinesTestRule
import com.pratama.dicodingstory.utils.DataDummy
import com.pratama.dicodingstory.utils.PagedTestDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepoTest {

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var storyDatabase: StoryDatabase

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var storyRepoMock: StoryRepo

    private lateinit var storyRepo: StoryRepo

    private val dummyToken = "authentication_token"
    private val dummyMultipart = DataDummy.generateDummyMultipartFile()
    private val dummyDescription = DataDummy.generateDummyRequestBody()
    private val dummyStoriesResponse = DataDummy.generateDummyStoriesResponse()

    @Before
    fun setup() {
        storyRepo = StoryRepo(storyDatabase, apiService)
    }

    @Test
    fun `Get stories with pager - successfully`() = runTest {
        val dummyStories = DataDummy.generateDummyListStory()
        val data = PagedTestDataSource.snapshot(dummyStories)

        val expectedResult = flowOf(data)

        `when`(storyRepoMock.getAllStories(dummyToken)).thenReturn(expectedResult)

        storyRepoMock.getAllStories(dummyToken).collect { actualResult ->
            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryListAdapter.DiffCallback,
                updateCallback = noopListUpdateCallback,
                mainDispatcher = coroutinesTestRule.testDispatcher,
                workerDispatcher = coroutinesTestRule.testDispatcher
            )
            differ.submitData(actualResult)

            Assert.assertNotNull(differ.snapshot())
            Assert.assertEquals(
                dummyStoriesResponse.storyResponseItems.size,
                differ.snapshot().size
            )
        }

    }

    @Test
    fun `Get stories with location - successfully`() = runTest {
        val expectedResult = flowOf(Result.success(dummyStoriesResponse))

        `when`(storyRepoMock.getAllStoriesWithLocation(dummyToken)).thenReturn(expectedResult)

        storyRepoMock.getAllStoriesWithLocation(dummyToken).collect { result ->
            Assert.assertTrue(result.isSuccess)
            Assert.assertFalse(result.isFailure)

            result.onSuccess { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertEquals(dummyStoriesResponse, actualResponse)
            }
        }
    }

    @Test
    fun `Get stories with location - throw exception`() = runTest {
        val expectedResponse = flowOf<Result<StoriesResponse>>(Result.failure(Exception("failed")))

        `when`(storyRepoMock.getAllStoriesWithLocation(dummyToken)).thenReturn(
            expectedResponse
        )

        storyRepoMock.getAllStoriesWithLocation(dummyToken).collect { result ->
            Assert.assertFalse(result.isSuccess)
            Assert.assertTrue(result.isFailure)

            result.onFailure {
                Assert.assertNotNull(it)
            }
        }
    }

    @Test
    fun `Upload image file - successfully`() = runTest {
        val expectedResponse = DataDummy.generateDummyFileUploadResponse()

        `when`(
            apiService.uploadImage(
                dummyToken.generateBearerToken(),
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        ).thenReturn(expectedResponse)

        storyRepo.uploadImage(dummyToken, dummyMultipart, dummyDescription, null, null)
            .collect { result ->
                Assert.assertTrue(result.isSuccess)
                Assert.assertFalse(result.isFailure)

                result.onSuccess { actualResponse ->
                    Assert.assertEquals(expectedResponse, actualResponse)
                }
            }

        Mockito.verify(apiService)
            .uploadImage(
                dummyToken.generateBearerToken(),
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        Mockito.verifyNoInteractions(storyDatabase)
    }

    @Test
    fun `Upload image file - throw exception`() = runTest {

        `when`(
            apiService.uploadImage(
                dummyToken.generateBearerToken(),
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        ).then { throw Exception() }

        storyRepo.uploadImage(dummyToken, dummyMultipart, dummyDescription, null, null)
            .collect { result ->
                Assert.assertFalse(result.isSuccess)
                Assert.assertTrue(result.isFailure)

                result.onFailure {
                    Assert.assertNotNull(it)
                }
            }

        Mockito.verify(apiService).uploadImage(
            dummyToken.generateBearerToken(),
            dummyMultipart,
            dummyDescription,
            null,
            null
        )
    }

    private fun String.generateBearerToken(): String {
        return "Bearer $this"
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}