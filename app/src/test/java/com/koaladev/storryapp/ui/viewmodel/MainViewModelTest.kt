package com.koaladev.storryapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import app.cash.turbine.test
import com.koaladev.storryapp.data.pref.UserModel
import com.koaladev.storryapp.data.repository.UserRepository
import com.koaladev.storryapp.data.response.ListStoryItem
import com.koaladev.storryapp.data.response.StoryResponse
import com.koaladev.storryapp.data.retrofit.ApiServices
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel
    private val repository: UserRepository = mockk()
    private val apiService: ApiServices = mockk()
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
fun `when get stories is successful`() = runBlockingTest {
    // Given
    val dummyStories = listOf(
        ListStoryItem(
            id = "1", name = "Story 1", description = "Desc 1",
            photoUrl = "photoUrl1",
            createdAt = "2022-01-01T12:00:00Z",
            lat = 10.2,
            lon = 12.5
        ),
        ListStoryItem(
            id = "2", name = "Story 2", description = "Desc 2",
            photoUrl = "photoUrl2",
            createdAt = "2022-01-02T12:00:00Z",
            lat = 11.2,
            lon = 13.5
        ),
    )
    val dummyResponse = StoryResponse(
        error = false,
        message = "Stories fetched successfully",
        listStory = dummyStories
    )
    val call: Call<StoryResponse> = mockk()

    every { repository.getSession() } returns flowOf(UserModel("dummy_id", "dummy_name", "dummy_email", "dummy_token", true))
    every { apiService.getAllStories(any(), any()) } returns call
    every { call.enqueue(any()) } answers {
        val callback = args[0] as Callback<StoryResponse>
        callback.onResponse(call, Response.success(dummyResponse))
    }

    // When
    viewModel.getStories(false)

    // Then
    viewModel.stories.observeForever { result ->
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals("Story 1", result[0]?.name)
        assertEquals("Desc 1", result[0]?.description)
    }
}

    @Test
    fun `when get stories returns empty list`() = runBlockingTest {
        // Given
        val emptyResponse = StoryResponse(
            error = false,
            message = "No stories found",
            listStory = emptyList()
        )
        val call: Call<StoryResponse> = mockk()

        every { repository.getSession() } returns flowOf(UserModel("dummy_id", "dummy_name", "dummy_email", "dummy_token", true))
        every { apiService.getAllStories(any(), any()) } returns call
        every { call.enqueue(any()) } answers {
            val callback = args[0] as Callback<StoryResponse>
            callback.onResponse(call, Response.success(emptyResponse))
        }

        // When
        viewModel.getStories(false)

        // Then
        viewModel.stories.observeForever { result ->
            assertNotNull(result)
            assertEquals(0, result.size)
        }
    }
}