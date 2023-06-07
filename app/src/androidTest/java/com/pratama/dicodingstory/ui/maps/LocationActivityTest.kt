package com.pratama.dicodingstory.ui.maps

import android.widget.FrameLayout
import androidx.paging.ExperimentalPagingApi
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.pratama.dicodingstory.R
import com.pratama.dicodingstory.data.remote.retrofit.ApiConfig
import com.pratama.dicodingstory.utils.JsonConverter
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalPagingApi
@MediumTest
@HiltAndroidTest
class LocationActivityTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
        ApiConfig.API_BASE_URL_MOCK = "http://127.0.0.1:8080/"
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun launchLocationActivity_Success() {
        val activityScenario = ActivityScenario.launch(LocationActivity::class.java)

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResponse)


        Espresso.onView(ViewMatchers.withId(R.id.map))
            .check(ViewAssertions.matches(ViewMatchers.isAssignableFrom(FrameLayout::class.java)))

        activityScenario.close()
    }
}