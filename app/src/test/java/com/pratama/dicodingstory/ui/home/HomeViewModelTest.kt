package com.pratama.dicodingstory.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ListUpdateCallback
import com.pratama.dicodingstory.adapter.StoryListAdapter
import com.pratama.dicodingstory.data.repository.AuthRepo
import com.pratama.dicodingstory.data.repository.StoryRepo
import com.pratama.dicodingstory.utils.CoroutinesTestRule
import com.pratama.dicodingstory.utils.DataDummy
import com.pratama.dicodingstory.utils.PagedTestDataSource
import com.pratama.dicodingstory.utils.getOrAwaitValue
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@HiltAndroidTest
class HomeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var storyRepo: StoryRepo

    @Inject
    lateinit var authRepo: AuthRepo

    private val dummyToken = "authentication_token"

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `Get all stories successfully`() = runBlockingTest {
        val dummyStories = DataDummy.generateDummyListStory()
        val data = PagedTestDataSource.snapshot(dummyStories)

        val stories = flowOf(data)

        `when`(storyRepo.getAllStories(dummyToken)).thenReturn(stories)

        val homeViewModel = HomeViewModel(storyRepo, authRepo)

        val actualStories = homeViewModel.getAllStories(dummyToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )
        differ.submitData(actualStories)

        advanceUntilIdle()

        verify(storyRepo).getAllStories(dummyToken)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
    }


    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}